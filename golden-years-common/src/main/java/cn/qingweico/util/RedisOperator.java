package cn.qingweico.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author zqw
 * @date 2021/9/5
 */
@Component
public class RedisOperator {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Key(键)简单的key-value操作

    /**
     * 判断key是否存在
     *
     * @param key key
     * @return {@code key} exist or not
     */
    public Boolean keyIsExist(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 实现命令:TTL key, 以秒为单位, 返回给定 key的剩余生存时间(TTL, time to live)
     *
     * @param key key
     * @return TTL
     */
    public Long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 实现命令:expire 设置过期时间, 单位秒
     *
     * @param key key
     */
    public void expire(String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    public void expire(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 实现命令:increment key, 增加key一次
     *
     * @param key key
     * @return increment key
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 实现命令:decrement key, 减少key一次
     *
     * @param key key
     */
    public void decrement(String key, long delta) {
        redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 实现命令:KEYS pattern, 查找所有符合给定模式 pattern的 key
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 实现命令:DEL key, 删除一个key
     *
     * @param key key
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除key
     *
     * @param keys keys
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }


    // String(字符串)

    /**
     * 实现命令:SET key value, 设置一个key-value(将字符串值 value关联到 key)
     *
     * @param key   key
     * @param value value
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 实现命令:SET key value EX seconds, 设置key-value和超时时间(秒)
     *
     * @param key     key
     * @param value   value
     * @param timeout (s)
     */
    public void set(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 如果key不存在, 则设置, 如果存在, 则报错
     *
     * @param key   key
     * @param value value
     */
    public void setnx60s(String key, String value) {
        redisTemplate.opsForValue().setIfAbsent(key, value, 60, TimeUnit.SECONDS);
    }

    /**
     * zset add
     *
     * @param key   k
     * @param value v
     * @param score score
     */
    public void zsetAdd(String key, String value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    public Set<String> zsetRevRange(String key, long l, long r) {
        return redisTemplate.opsForZSet().reverseRange(key, l, r);
    }

    public Long zsetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }


    /**
     * 如果key不存在, 则设置, 如果存在, 则报错
     *
     * @param key   key
     * @param value value
     */
    public void setnx(String key, String value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().setIfAbsent(key, value, time, timeUnit);
    }

    /**
     * 如果key不存在, 则设置, 如果存在, 则报错
     *
     * @param key   key,
     * @param value value
     */
    public void setnx(String key, String value) {
        redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 实现命令:GET key, 返回 key所关联的字符串值
     *
     * @param key key
     * @return value
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 批量查询, 对应multi get
     *
     * @param keys {@code List<String>} keys
     * @return {@code List<String>} value
     */
    public List<String> mget(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 批量查询, 管道pipeline
     *
     * @param keys {@code List<String>} keys
     * @return {@code List<Object>} value
     */
    public List<Object> batchGet(List<String> keys) {

        // nginx -> keepalive
        // redis -> pipeline
        return redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            StringRedisConnection src = (StringRedisConnection) connection;

            for (String k : keys) {
                src.get(k);
            }
            return null;
        });
    }


    // Hash(哈希表)

    /**
     * 实现命令:HSET key field value, 将哈希表 key中的域 field的值设为 value
     *
     * @param key   key
     * @param field field
     * @param value value
     */
    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 实现命令:HGET key field, 返回哈希表 key中给定域 field的值
     *
     * @param key   key
     * @param field field
     * @return value
     */
    public String hget(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 实现命令:HDEL key field [field ...], 删除哈希表 key 中的一个或多个指定域, 不存在的域将被忽略
     *
     * @param key    key
     * @param fields fields[]
     */
    public void hdel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 实现命令:HGETALL key, 返回哈希表 key中, 所有的域和值
     *
     * @param key key
     * @return <field, value>
     */
    public Map<Object, Object> hgetall(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    // List(列表)

    /**
     * 实现命令:LPUSH key value, 将一个值 value插入到列表 key的表头
     *
     * @param key   key
     * @param value value
     * @return 执行 LPUSH命令后, 列表的长度
     */
    public Long lpush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 实现命令:LPOP key, 移除并返回列表 key的头元素
     *
     * @param key key
     * @return 列表key的头元素
     */
    public String lpop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 实现命令:RPUSH key value, 将一个值 value插入到列表 key的表尾(最右边)
     *
     * @param key   key
     * @param value value
     * @return 执行 LPUSH命令后, 列表的长度
     */
    public Long rpush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }
}
