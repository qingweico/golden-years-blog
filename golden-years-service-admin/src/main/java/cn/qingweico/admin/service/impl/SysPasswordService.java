package cn.qingweico.admin.service.impl;

import cn.qingweico.core.config.limit.RateLimiterHandler;
import cn.qingweico.core.security.context.AuthenticationContextHolder;
import cn.qingweico.entity.model.LoginLimit;
import cn.qingweico.entity.model.LoginUser;
import cn.qingweico.exception.TooManyAttemptsException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.util.SecurityUtils;
import cn.qingweico.util.redis.RedisCache;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 登录密码方法
 *
 * @author zqw
 * @date 2023-04-29
 */
@Slf4j
@Component
public class SysPasswordService {
    @Resource
    private RedisCache redisCache;
    private static final String KEY = "PasswordLogin";

    @Value("${gy.token-exp:1800}")
    public long tokenExp;

    @Value("${gy.login-attempts-limit-times:100}")
    public int loginAttemptsLimitTimes;

    @Value("${gy.login-attempts-limit-seconds:300}")
    public int loginAttemptsLimitSeconds;

    @Setter
    private LoginLimit loginLimit = new LoginLimit(60, 20);
    @Setter
    private RateLimiterHandler rateLimiterHandler;


    public void validate(LoginUser loginUser) {
        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext();
        String username = usernamePasswordAuthenticationToken.getName();
        String password = usernamePasswordAuthenticationToken.getCredentials().toString();
        if (!rateLimiterHandler.limit(this.getClass().getSimpleName(), KEY, loginLimit.getLimitPeriod(), loginLimit.getLimitCount())) {
            log.warn("{} try too many {}", username, this.getClass().getSimpleName());
            throw new TooManyAttemptsException("Too many attempts");
        }
        if (tooManyAttempts(loginUser.getUserId())) {
            log.warn("{} try too many login", username);
            throw new TooManyAttemptsException("Too many attempts");
        }
        if (!matches(loginUser, password)) {
            // TODO 记录登录日志
            addAttempts(loginUser.getUserId());
            throw new BadCredentialsException("invalid identifier or certificate");
        }
        if (!loginUser.isEnabled()) {
            // TODO 记录登录日志
            addAttempts(loginUser.getUserId());
            throw new DisabledException("The user has been disabled");
        }
        // TODO 记录登录日志
        // 清空尝试次数
        deleteAttempts(loginUser.getUserId());
    }

    private boolean matches(LoginUser loginUser, String rawPassword) {
        return SecurityUtils.matchesPassword(rawPassword, loginUser.getPassword());
    }

    private void addAttempts(String accountId) {
        String key = getAttemptsUserKey(accountId);
        String attempts = redisCache.get(key);
        if (StringUtils.isEmpty(attempts)) {
            redisCache.set(key, "1", loginAttemptsLimitSeconds);
        } else {
            redisCache.increment(key, 1);
        }
        log.info("Attempts:{}", attempts);
    }

    private boolean tooManyAttempts(String userId) {
        String key = getAttemptsUserKey(userId);
        String attempts = redisCache.get(key);
        if (StringUtils.isEmpty(attempts)) {
            return false;
        }
        return NumberUtils.toInt(attempts) >= loginAttemptsLimitSeconds;
    }

    private String getAttemptsUserKey(String userId) {
        return RedisConst.USER_ATTEMPT_LOGIN + userId;
    }

    private void deleteAttempts(String userId) {
        redisCache.del(getAttemptsUserKey(userId));
    }

}
