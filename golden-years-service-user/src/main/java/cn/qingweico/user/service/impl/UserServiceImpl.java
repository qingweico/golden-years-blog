package cn.qingweico.user.service.impl;

import cn.qingweico.entity.User;
import cn.qingweico.entity.model.UserInfoBO;
import cn.qingweico.enums.Sex;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.user.mapper.UserMapper;
import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.SnowflakeIdWorker;
import cn.qingweico.util.redis.RedisCache;
import cn.qingweico.util.redis.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zqw
 * @date 2021/9/6
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private LoginLogService loginLogService;
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedisCache redisCache;
    @Resource
    private UserMapper userMapper;

    private final static String[] FACE = {
            "https://cdn.qingweico.cn/blog/274080b281e5f0d31b7390207d78f591.jpeg",
            "https://cdn.qingweico.cn/blog/a21f7fc6328bcb72d1a0d9647b3b93c9.jpg",
            "https://cdn.qingweico.cn/blog/0f6087e207dd169cbe300863110f98d0.jpeg"
    };

    // TODO 使用系统变量

    private final static String DEFAULT_PASSWORD = "123456";

    @Override
    public PagedResult queryUserList(String nickname,
                                     Integer status,
                                     String mobile,
                                     Date startDate,
                                     Date endDate,
                                     Integer pageNo,
                                     Integer pageSize) {

        LambdaQueryWrapper<User> lwq = new LambdaQueryWrapper<>();
        lwq.orderByDesc(User::getCreateTime);
        if (StringUtils.isNotBlank(nickname)) {
            lwq.like(User::getNickname, SysConst.DELIMITER_PERCENT + nickname + SysConst.DELIMITER_PERCENT);
        }
        if (StringUtils.isNotBlank(mobile)) {
            lwq.like(User::getMobile, SysConst.DELIMITER_PERCENT + mobile + SysConst.DELIMITER_PERCENT);
        }
        if (UserStatus.isUserStatusValid(status)) {
            lwq.eq(User::getAvailable, status);
        }
        lwq.ge(startDate != null, User::getCreateTime, startDate);
        lwq.le(startDate != null, User::getCreateTime, startDate);

        Page<User> page = new Page<>(pageNo, pageSize);
        userMapper.selectPage(page, lwq);
        return null;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void changeUserStatus(String userId, Integer doStatus) {
        User user = new User();
        user.setId(userId);
        user.setAvailable(doStatus);
        if (userMapper.updateById(user) > 0) {
            String key = RedisConst.REDIS_USER_INFO + SysConst.SYMBOL_COLON + userId;
            redisUtil.clearCache(key);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }

    }

    @Override
    public User queryUserByMobile(String mobile) {
        LambdaQueryWrapper<User> lwq = new LambdaQueryWrapper<>();
        lwq.eq(User::getMobile, mobile);
        return userMapper.selectOne(lwq);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public User createUser(String mobile) {
        User user = new User();
        String userId = SnowflakeIdWorker.nextId();
        user.setId(userId);
        String randomFace = FACE[(int) (Math.random() * 3)];
        user.setMobile(mobile);
        user.setNickname(mobile);
        user.setFace(randomFace);
        user.setPassword(DEFAULT_PASSWORD);
        user.setBirthday(DateUtils.stringToDate("1990-07-01"));
        user.setAvailable(UserStatus.AVAILABLE.getVal());
        user.setSex(Sex.SECRET.getVal());
        user.setCreateTime(DateUtils.nowDateTime());
        user.setUpdateTime(DateUtils.nowDateTime());
        if (userMapper.insert(user) < 0) {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
        return user;
    }

    @Override
    public User queryUserById(String userId) {
        return userMapper.selectById(userId);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void updateUserInfo(UserInfoBO userInfoBO) {

        String userId = userInfoBO.getId();
        User userInfo = new User();

        BeanUtils.copyProperties(userInfoBO, userInfo);

        userInfo.setUpdateTime(DateUtils.nowDateTime());
        userInfo.setAvailable(UserStatus.AVAILABLE.getVal());

        // 保证缓存数据双写一致性
        Lock lock = new ReentrantLock();
        CountDownLatch updateCacheLatch = new CountDownLatch(1);
        lock.lock();
        try {
            updateById(userInfo);
        } finally {
            lock.unlock();
            updateCacheLatch.countDown();
        }
        try {
            updateCacheLatch.await();
        } catch (InterruptedException e) {
            log.error("{}", e.getMessage());
        }
        // 查询数据库中最新的数据放入缓存中
        User user = queryUserById(userId);
        redisCache.set(RedisConst.REDIS_USER_INFO + SysConst.SYMBOL_COLON + userId, JsonUtils.objectToJson(user));
    }

    @Override
    public User queryUserByAuth(String auth) {
        LambdaQueryWrapper<User> lwq = new LambdaQueryWrapper<>();
        lwq.eq(User::getMobile, auth)
                .or().eq(User::getNickname, auth)
                .or().eq(User::getEmail, auth);
        return userMapper.selectOne(lwq);
    }

    @Override
    public void doSaveUserAuthToken(User user, String token) {
        // 保存token以及用户信息到redis中
        redisCache.set(RedisConst.REDIS_USER_TOKEN + SysConst.SYMBOL_COLON + user.getId(), token);
        redisCache.set(RedisConst.REDIS_USER_INFO + SysConst.SYMBOL_COLON + user.getId(), JsonUtils.objectToJson(user));
    }


    @Override
    public void doSaveLoginLog(String userId) {
        // 保存用户登陆日志信息
        loginLogService.saveUserLoginLog(userId);
    }

    @Override
    public void resetPasswords(String userId) {
        User user = new User();
        user.setId(userId);
        user.setPassword(DEFAULT_PASSWORD);
        if (userMapper.updateById(user) > 0) {
            String key = RedisConst.REDIS_USER_INFO;
            redisUtil.clearCache(key);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void alterPwd(String userId, String newPassword) {
        User user = queryUserById(userId);
        user.setPassword(newPassword);
        if (userMapper.updateById(user) > 0) {
            String key = RedisConst.REDIS_USER_INFO;
            redisUtil.clearCache(key);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void alterMobile(User user, String newMobile) {
        user.setMobile(newMobile);
        if (userMapper.updateById(user) > 0) {
            String cachedUserKey = RedisConst.REDIS_USER_INFO + SysConst.DELIMITER_COLON + user.getId();
            redisUtil.clearCache(cachedUserKey);
        }
    }

    @Override
    public Integer queryUserCounts() {
        LambdaQueryWrapper<User> lwq = new LambdaQueryWrapper<>();
        lwq.eq(User::getAvailable, UserStatus.AVAILABLE.getVal());
        return userMapper.selectCount(lwq);
    }
}
