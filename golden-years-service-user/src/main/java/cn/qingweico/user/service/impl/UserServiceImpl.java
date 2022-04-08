package cn.qingweico.user.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.enums.Sex;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.Constants;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.bo.UpdatePwdBO;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.User;
import cn.qingweico.pojo.bo.UserInfoBO;
import cn.qingweico.user.mapper.UserMapper;
import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.DesensitizationUtil;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
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

    private final static String[] FACE = {
            "https://cdn.qingweico.cn/blog/274080b281e5f0d31b7390207d78f591.jpeg",
            "https://cdn.qingweico.cn/blog/a21f7fc6328bcb72d1a0d9647b3b93c9.jpg",
            "https://cdn.qingweico.cn/blog/0f6087e207dd169cbe300863110f98d0.jpeg"
    };
    @Resource
    private UserMapper userMapper;

    @Resource
    private Sid sid;

    @Override
    public PagedGridResult queryUserList(String nickname,
                                         Integer status,
                                         Date startDate,
                                         Date endDate,
                                         Integer page,
                                         Integer pageSize) {

        Example example = new Example(User.class);
        example.orderBy("createdTime").desc();
        Example.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(nickname)) {
            criteria.andLike("nickname", "%" + nickname + "%");
        }

        if (UserStatus.isUserStatusValid(status)) {
            criteria.andEqualTo("activeStatus", status);
        }

        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo("createdTime", startDate);
        }
        if (endDate != null) {
            criteria.andGreaterThanOrEqualTo("createdTime", endDate);
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
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User queryMobileIsPresent(String mobile) {

        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("mobile", mobile);
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
        user.setNickname("用户: " + DesensitizationUtil.commonDisplay(mobile));
        user.setFace(randomFace);
        user.setBirthday(DateUtils.stringToDate("1990-07-01"));
        user.setActiveStatus(UserStatus.INACTIVE.type);
        user.setSex(Sex.SECRET.type);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        userMapper.insert(user);
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

        userInfo.setUpdatedTime(new Date());
        userInfo.setActiveStatus(UserStatus.ACTIVE.type);

        // 保证缓存数据双写一致性
        Lock lock = new ReentrantLock();
        CountDownLatch updateCacheLatch = new CountDownLatch(1);
        lock.lock();
        try {
            int res = userMapper.updateByPrimaryKeySelective(userInfo);
            if (res != 1) {
                GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
            }
        } finally {
            lock.unlock();
            updateCacheLatch.countDown();
        }
        try {
            updateCacheLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 查询数据库中最新的数据放入缓存中
        User user = queryUserById(userId);
        log.info("updateUserInfo: 缓存已更新");
        redisOperator.set(RedisConf.REDIS_USER_INFO + Constants.SYMBOL_COLON + userId, JsonUtils.objectToJson(user));
    }

    @Override
    public User queryUserByAuth(String auth) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mobile", auth);
        criteria.orEqualTo("nickname", auth);
        criteria.orEqualTo("email", auth);
        return userMapper.selectOneByExample(example);
    }

    @Override
    @Async("asyncTask")
    public void doSaveUserAuthToken(User user, String token) {
        // 保存token以及用户信息到redis中
        redisOperator.set(RedisConf.REDIS_USER_TOKEN + Constants.SYMBOL_COLON + user.getId(), token);
        redisOperator.set(RedisConf.REDIS_USER_INFO + Constants.SYMBOL_COLON + user.getId(), JsonUtils.objectToJson(user));
    }

    @Async("asyncTask")
    @Override
    public void doSaveLoginLog(String userId) {
        // 保存用户登陆日志信息
        loginLogService.saveUserLoginLog(userId);
    }

    @Override
    public void resetPasswords(String userId) {
        User user = new User();
        user.setId(userId);
        user.setPassword("123456");
        if (userMapper.updateByPrimaryKeySelective(user) > 0) {
            String key = RedisConf.REDIS_USER_INFO;
            refreshCache(key);
        } else {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void alterPwd(UpdatePwdBO updatePwdBO) {
        User user = new User();
        String id = updatePwdBO.getUserId();
        String newPassword = updatePwdBO.getNewPassword();
        user.setId(id);
        user.setPassword(newPassword);
        if (userMapper.updateByPrimaryKeySelective(user) > 0) {
            String key = RedisConf.REDIS_USER_INFO;
            refreshCache(key);
        } else {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }
}