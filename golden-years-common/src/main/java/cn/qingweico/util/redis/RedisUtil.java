package cn.qingweico.util.redis;

import cn.hutool.core.util.BooleanUtil;
import cn.qingweico.entity.User;
import cn.qingweico.entity.model.RedisData;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.JwtUtils;
import cn.qingweico.util.ServletReqUtils;
import cn.qingweico.util.StringUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Redis 工具类
 *
 * @author zqw
 * @date 2022/8/26
 */
@Slf4j
@Component
public final class RedisUtil {

    @Resource
    RedisCache redisCache;
    @Resource
    TaskExecutor taskExecutor;


    /*设置数据逻辑过期*/

    public void setLogicExpire(String key, Object value, long timeout, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setDate(LocalDateTime.now().plusSeconds(unit.toSeconds(timeout)));
        // 不再设置ttl,判断逻辑过期字段expireTime即可
        redisCache.set(key, JsonUtils.objectToJson(redisData));
    }


    /*解决缓存穿透: 设置空值*/

    public <R, ID> R resolveCachePenetrate(String keyPrefix,
                                           ID id, Class<R> type, Function<ID, R> callback,
                                           long timeout, TimeUnit unit) {
        String key = keyPrefix + id;
        final String json = redisCache.get(key);
        if (StringUtils.isNotBlank(json)) {
            return JsonUtils.jsonToPojo(json, type);
        }
        // 判断 json 是否为 ""
        if (StringUtils.EMPTY.equals(json)) {
            return null;
        }
        // 什么都没命中 再去查询数据库
        R r = callback.apply(id);
        if (r == null) {
            // 缓存信息不存在,设置缓存为空值并设置ttl为2分钟;防止缓存穿透,下次查询时不会查询数据库
            redisCache.setNx(key, StringUtils.EMPTY, RedisConst.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 写入缓存
        redisCache.setNx(key, JsonUtils.objectToJson(r), timeout, unit);
        return r;
    }
    /*解决缓存击穿: 逻辑过期 + 互斥锁重建缓存*/

    public <R, ID> R queryLogicExpire(String keyPrefix, ID id,
                                      Class<R> type, Function<ID, R> callback,
                                      long timeout, TimeUnit unit) {
        String key = keyPrefix + id;
        final String json = redisCache.get(key);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        RedisData rd = JsonUtils.jsonToPojo(json, RedisData.class);
        assert rd != null;
        @SuppressWarnings("unchecked")
        R r = (R) rd.getData();
        LocalDateTime expireTime = rd.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 未过期
            return r;
        }
        // 缓存已过期 重建缓存
        // 构建分布式锁
        // 获取锁
        String lock = RedisConst.LOCK + id;
        boolean isSuccess = redisCache.setNx(lock, RedisConst.LOCK_VALUE,
                RedisConst.LOCK_TIME, TimeUnit.SECONDS);
        // avoid unpacking npe!!
        isSuccess = BooleanUtil.isTrue(isSuccess);
        if (isSuccess) {
            taskExecutor.execute(() -> {
                try {
                    final R apply = callback.apply(id);
                    this.setLogicExpire(key, apply, timeout, unit);
                } finally {
                    redisCache.del(lock);
                }
            });
        }
        // 未拿到锁 直接返回过期的缓存信息即可
        return r;
    }

    /**
     * 获取已登录用户的信息
     *
     * @return User
     */
    public User getLoginUser() {
        String token = ServletReqUtils.getRequest().getHeader("Authorization");
        // 客户端 token 为空, 用户未登录
        if(StringUtils.isEmpty(token)) {
            GraceException.error(Response.UN_LOGIN);
            return null;
        }
        // 客户端 token 不为空, 验证服务端JWT是否过期
        String userId = StringUtils.EMPTY;
        User user = null;
        String loginUserKey = RedisConst.LOGIN_USER_KEY;
        String loginTokenKey = RedisConst.LOGIN_TOKEN_KEY;
        try {
            Claims claims = JwtUtils.parseJwt(token);
            if (claims != null) {
                userId = claims.get(SysConst.USER_ID, String.class);
                user = JsonUtils.jsonToPojo(redisCache.get(loginUserKey + userId), User.class);
            }
        } catch (Exception e) {
            // 服务端JWT已过期
            GraceException.error(Response.TOKEN_EXPIRATION);
        }
        if (!StringUtils.isEmpty(userId)) {
            String redisUserToken = redisCache.get(loginTokenKey + userId);
            if (StringUtils.isEmpty(redisUserToken) || !token.equals(redisUserToken)) {
                // Redis 中 token 已过期
                GraceException.error(Response.TOKEN_EXPIRATION);
            }
        } else {
            GraceException.error(Response.TOKEN_EXPIRATION);
        }
        return user;
    }


    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);

    }

    /**
     * 使用 setnx 命令 + lua 脚本 构建分布式锁
     *
     * @return 在超时时间内是否获取到锁
     */
    public boolean tryLock(String key, String requestId) {
        String keyPrefix = RedisConst.LOCK;
        Boolean result = redisCache.setNx(keyPrefix + key, requestId, RedisConst.LOCK_TTL, TimeUnit.SECONDS);
        /// !!!avoid npe
        return Boolean.TRUE.equals(result);
    }

    public void unlock(String key, String requestId) {
        String keyPrefix = RedisConst.LOCK;
        redisCache.execute(UNLOCK_SCRIPT, keyPrefix + key, requestId);
    }

    public String getUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 移除跟实体类相关的redis key-value
     */
    public void clearCache(String key, boolean isMulti) {
        if (isMulti) {
            Set<String> keySet = redisCache.keys(key + "*");
            for (String k : keySet) {
                // 逐条删除
                redisCache.del(k);
            }
        } else {
            if (redisCache.keyPresent(key)) {
                redisCache.del(key);
            }
        }
    }

    public void clearCache(String key) {
        clearCache(key, false);
    }

    /**
     * 移除以{@code Prefix}为前缀的缓存
     *
     * @param keyPrefix keyPrefix
     */
    public void clearPrefixCache(String keyPrefix) {
        Set<String> keySet = redisCache.keys(keyPrefix + "*");
        for (String key : keySet) {
            redisCache.del(key);
        }
    }
}
