package cn.qingweico.user.service.impl;

import cn.qingweico.api.config.RabbitMqConfig;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.enums.Sex;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConf;
import cn.qingweico.global.RedisConf;
import cn.qingweico.result.Response;
import cn.qingweico.pojo.User;
import cn.qingweico.pojo.bo.UserInfoBO;
import cn.qingweico.user.mapper.UserMapper;
import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.DesensitizationUtil;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedResult;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zqw
 * @date 2021/9/6
 */
@Slf4j
@Service
public class UserServiceImpl extends BaseService implements UserService {
    @Resource
    private LoginLogService loginLogService;
    @Resource
    private UserMapper userMapper;

    private final static String[] FACE = {
            "https://cdn.qingweico.cn/blog/274080b281e5f0d31b7390207d78f591.jpeg",
            "https://cdn.qingweico.cn/blog/a21f7fc6328bcb72d1a0d9647b3b93c9.jpg",
            "https://cdn.qingweico.cn/blog/0f6087e207dd169cbe300863110f98d0.jpeg"
    };
    private final static String DEFAULT_PASSWORD = "123456";

    @Override
    public PagedResult queryUserList(String nickname,
                                     Integer status,
                                     String mobile,
                                     Date startDate,
                                     Date endDate,
                                     Integer page,
                                     Integer pageSize) {

        Example example = new Example(User.class);
        example.orderBy(SysConf.CREATE_TIME).desc();
        Example.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(nickname)) {
            criteria.andLike(SysConf.NICK_NAME, SysConf.DELIMITER_PERCENT + nickname + SysConf.DELIMITER_PERCENT);
        }
        if (StringUtils.isNotBlank(mobile)) {
            criteria.andLike(SysConf.MOBILE, SysConf.DELIMITER_PERCENT + mobile + SysConf.DELIMITER_PERCENT);
        }
        if (UserStatus.isUserStatusValid(status)) {
            criteria.andEqualTo(SysConf.ACTIVE_STATUS, status);
        }

        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo(SysConf.CREATE_TIME, startDate);
        }
        if (endDate != null) {
            criteria.andGreaterThanOrEqualTo(SysConf.CREATE_TIME, endDate);
        }

        PageHelper.startPage(page, pageSize);
        List<User> userList = userMapper.selectByExample(example);
        return setterPagedGrid(userList, page);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void freezeUserOrNot(String userId, Integer doStatus) {
        User user = new User();
        user.setId(userId);
        user.setActiveStatus(doStatus);
        if (userMapper.updateByPrimaryKeySelective(user) > 0) {
            String key = RedisConf.REDIS_USER_INFO + SysConf.SYMBOL_COLON + userId;
            refreshCache(key);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }

    }

    @Override
    public User queryMobileIsPresent(String mobile) {

        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo(SysConf.MOBILE, mobile);
        return userMapper.selectOneByExample(userExample);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public User createUser(String mobile) {
        String userId = sid.nextShort();
        User user = new User();
        user.setId(userId);
        String randomFace = FACE[(int) (Math.random() * 3)];
        user.setMobile(mobile);
        user.setNickname(DesensitizationUtil.commonDisplay(mobile));
        user.setFace(randomFace);
        // 设置默认密码
        user.setPassword(DEFAULT_PASSWORD);
        user.setBirthday(DateUtils.stringToDate("1990-07-01"));
        user.setActiveStatus(UserStatus.INACTIVE.type);
        user.setSex(Sex.SECRET.type);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        // 放 rabbitmq 中, 为用户创建默认的收藏夹
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_ARTICLE,
                SysConf.ARTICLE_CREATE_FAVORITES_DO, userId);
        if (userMapper.insert(user) < 0) {
            log.error("create user error");
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
        return user;
    }

    @Override
    public User queryUserById(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    @Async("asyncTask")
    public void updateUserInfo(UserInfoBO userInfoBO) {

        String userId = userInfoBO.getId();
        User userInfo = new User();

        BeanUtils.copyProperties(userInfoBO, userInfo);

        userInfo.setUpdateTime(new Date());
        userInfo.setActiveStatus(UserStatus.ACTIVE.type);

        // 保证缓存数据双写一致性
        Lock lock = new ReentrantLock();
        CountDownLatch updateCacheLatch = new CountDownLatch(1);
        lock.lock();
        try {
            int updatedNum = userMapper.updateByPrimaryKeySelective(userInfo);
            if (updatedNum != 1) {
                GraceException.error(Response.USER_UPDATE_ERROR);
            }
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
        redisTemplate.set(RedisConf.REDIS_USER_INFO + SysConf.SYMBOL_COLON + userId, JsonUtils.objectToJson(user));
        log.info("update user info: cache has been updated");
    }

    @Override
    public User queryUserByAuth(String auth) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SysConf.MOBILE, auth);
        criteria.orEqualTo(SysConf.NICK_NAME, auth);
        criteria.orEqualTo(SysConf.EMAIL, auth);
        return userMapper.selectOneByExample(example);
    }

    @Override
    @Async("asyncTask")
    public void doSaveUserAuthToken(User user, String token) {
        // 保存token以及用户信息到redis中
        redisTemplate.set(RedisConf.REDIS_USER_TOKEN + SysConf.SYMBOL_COLON + user.getId(), token);
        redisTemplate.set(RedisConf.REDIS_USER_INFO + SysConf.SYMBOL_COLON + user.getId(), JsonUtils.objectToJson(user));
    }


    @Override
    @Async("asyncTask")
    public void doSaveLoginLog(String userId) {
        // 保存用户登陆日志信息
        loginLogService.saveUserLoginLog(userId);
    }

    @Override
    public void resetPasswords(String userId) {
        User user = new User();
        user.setId(userId);
        user.setPassword(DEFAULT_PASSWORD);
        if (userMapper.updateByPrimaryKeySelective(user) > 0) {
            String key = RedisConf.REDIS_USER_INFO;
            refreshCache(key);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void alterPwd(String userId, String newPassword) {
        User user = queryUserById(userId);
        user.setPassword(newPassword);
        if (userMapper.updateByPrimaryKeySelective(user) > 0) {
            String key = RedisConf.REDIS_USER_INFO;
            refreshCache(key);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void alterMobile(User user, String newMobile) {
        user.setMobile(newMobile);
        if (userMapper.updateByPrimaryKeySelective(user) > 0) {
            String cachedUserKey = RedisConf.REDIS_USER_INFO + SysConf.DELIMITER_COLON + user.getId();
            refreshCache(cachedUserKey);
        } else {
            log.error("update user mobile error");
        }
    }

    @Override
    public Integer queryUserCounts() {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo(SysConf.ACTIVE_STATUS, UserStatus.INACTIVE.type);
        return userMapper.selectCountByExample(example);
    }
}