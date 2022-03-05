package cn.qingweico.user.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.enums.Sex;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.Constants;
import cn.qingweico.global.RedisConf;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.AppUser;
import cn.qingweico.pojo.bo.UpdateUserInfoBO;
import cn.qingweico.user.mapper.AppUserMapper;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.DesensitizationUtil;
import cn.qingweico.util.JsonUtils;
import org.n3r.idworker.Sid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zqw
 * @date 2021/9/6
 */
@Service
public class UserServiceImpl extends BaseService implements UserService {


    private final static String[] FACE = {
            "https://cdn.qingweico.cn/blog/274080b281e5f0d31b7390207d78f591.jpeg",
            "https://cdn.qingweico.cn/blog/a21f7fc6328bcb72d1a0d9647b3b93c9.jpg",
            "https://cdn.qingweico.cn/blog/0f6087e207dd169cbe300863110f98d0.jpeg"
    };
    @Resource
    private AppUserMapper userMapper;

    @Resource
    private Sid sid;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public AppUser queryMobileIsExist(String mobile) {

        Example userExample = new Example(AppUser.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("mobile", mobile);
        return userMapper.selectOneByExample(userExample);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public AppUser createUser(String mobile) {

        String userId = sid.nextShort();
        AppUser appUser = new AppUser();
        appUser.setId(userId);
        String randomFace = FACE[(int) (Math.random() * 3)];
        appUser.setMobile(mobile);
        appUser.setNickname("用户: " + DesensitizationUtil.commonDisplay(mobile));
        appUser.setFace(randomFace);
        appUser.setBirthday(DateUtils.stringToDate("1990-07-01"));
        appUser.setActiveStatus(UserStatus.INACTIVE.type);
        appUser.setSex(Sex.SECRET.type);
        appUser.setCreatedTime(new Date());
        appUser.setUpdatedTime(new Date());
        userMapper.insert(appUser);
        return appUser;
    }

    @Override
    public AppUser queryUserById(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    @Async("asyncTask")
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {

        String userId = updateUserInfoBO.getId();
        AppUser userInfo = new AppUser();

        BeanUtils.copyProperties(updateUserInfoBO, userInfo);

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
        AppUser user = queryUserById(userId);
        log.info("updateUserInfo: 缓存已更新");
        redisOperator.set(RedisConf.REDIS_USER_INFO + Constants.SYMBOL_COLON + userId, JsonUtils.objectToJson(user));
    }

    @Override
    public AppUser queryUserByAuth(String auth) {
        Example example = new Example(AppUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mobile", auth);
        criteria.orEqualTo("nickname", auth);
        criteria.orEqualTo("email", auth);
        return userMapper.selectOneByExample(example);
    }
}