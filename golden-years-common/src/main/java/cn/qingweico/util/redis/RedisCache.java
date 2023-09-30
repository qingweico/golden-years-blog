package cn.qingweico.util.redis;

import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zqw
 * @date 2021/9/5
 */
@Component
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisCache {

    @Resource
    private StringRedisTemplate srt;

    @Resource
    private RedisTemplate redisTemplate;

    // Key(键)简单的key-value操作

    /**
     * 判断key是否存在
     *
     * @param key key
     * @return {@code key} exist or not
     */
    public Boolean keyPresent(String key) {
        return srt.hasKey(key);
    }

    /**
     * 实现命令:TTL key, 以秒为单位, 返回给定 key的剩余生存时间(TTL, time to live)
     *
     * @param key key
     * @return TTL
     */
    public Long ttl(String key) {
        return srt.getExpire(key);
    }

    /**
     * 实现命令:expire 设置过期时间, 单位秒
     *
     * @param key key
     */
    public void expire(String key, long timeout) {
        srt.expire(key, timeout, TimeUnit.SECONDS);
    }

    public void expire(String key, long timeout, TimeUnit timeUnit) {
        srt.expire(key, timeout, timeUnit);
    }

    /**
     * 实现命令:increment key, 增加key一次
     *
     * @param key key
     * @return increment key
     */
    public Long increment(String key, long delta) {
        return srt.opsForValue().increment(key, delta);
    }

    /**
     * 实现命令:decrement key, 减少key一次
     *
     * @param key key
     */
    public void decrement(String key, long delta) {
        srt.opsForValue().decrement(key, delta);
    }

    /**
     * 实现命令:KEYS pattern, 查找所有符合给定模式 pattern的 key
     */
    public Set<String> keys(String pattern) {
        return srt.keys(pattern);
    }

    /**
     * 实现命令:DEL key, 删除一个key
     *
     * @param key key
     */
    public void del(String key) {
        srt.delete(key);
    }

    /**
     * 批量删除key
     *
     * @param keys keys
     */
    public void delete(Collection<String> keys) {
        srt.delete(keys);
    }


    // String(字符串)

    /**
     * 实现命令:SET key value, 设置一个key-value(将字符串值 value关联到 key)
     *
     * @param key   key
     * @param value value
     */
    public void set(String key, String value) {
        srt.opsForValue().set(key, value);
    }

    /**
     * 实现命令:SET key value EX seconds, 设置key-value和超时时间(秒)
     *
     * @param key     key
     * @param value   value
     * @param timeout (s)
     */
    public void set(String key, String value, long timeout) {
        srt.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 如果key不存在, 则设置, 如果存在, 则报错
     *
     * @param key   key
     * @param value value
     */
    public void setNx60s(String key, String value) {
        srt.opsForValue().setIfAbsent(key, value, 60, TimeUnit.SECONDS);
    }

    /**
     * zset add
     *
     * @param key   k
     * @param value v
     * @param score score
     */
    public void zSetAdd(String key, String value, double score) {
        srt.opsForZSet().add(key, value, score);
    }

    public Set<String> zSetRevRange(String key, long l, long r) {
        return srt.opsForZSet().reverseRange(key, l, r);
    }

    public Long zSetSize(String key) {
        return srt.opsForZSet().size(key);
    }


    /**
     * 如果key不存在, 则设置, 如果存在, 则报错
     *
     * @param key   key
     * @param value value
     */
    public Boolean setNx(String key, String value, long time, TimeUnit timeUnit) {
        return srt.opsForValue().setIfAbsent(key, value, time, timeUnit);
    }

    /**
     * 如果key不存在, 则设置, 如果存在, 则报错
     *
     * @param key   key,
     * @param value value
     */
    public void setNx(String key, String value) {
        srt.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 实现命令:GET key, 返回 key所关联的字符串值
     *
     * @param key key
     * @return value
     */
    public String get(String key) {
        return srt.opsForValue().get(key);
    }

    /**
     * 批量查询, 对应multi get
     *
     * @param keys {@code List<String>} keys
     * @return {@code List<String>} value
     */
    public List<String> mGet(Collection<String> keys) {
        return srt.opsForValue().multiGet(keys);
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
        return srt.executePipelined((RedisCallback<String>) connection -> {
            StringRedisConnection src = (StringRedisConnection) connection;

            for (String k : keys) {
                src.get(k);
            }
            return null;
        });
    }


    // Hash(哈希表)

    /**
     * 实现命令:H SET key field value, 将哈希表 key中的域 field的值设为 value
     *
     * @param key   key
     * @param field field
     * @param value value
     */
    public void hSet(String key, String field, Object value) {
        srt.opsForHash().put(key, field, value);
    }

    /**
     * 实现命令:H GET key field, 返回哈希表 key中给定域 field的值
     *
     * @param key   key
     * @param field field
     * @return value
     */
    public String hGet(String key, String field) {
        return (String) srt.opsForHash().get(key, field);
    }

    /**
     * 实现命令:H DEL key field [field ...], 删除哈希表 key 中的一个或多个指定域, 不存在的域将被忽略
     *
     * @param key    key
     * @param fields fields[]
     */
    public void hDel(String key, Object... fields) {
        srt.opsForHash().delete(key, fields);
    }

    /**
     * 实现命令:H GET ALL key, 返回哈希表 key中, 所有的域和值
     *
     * @param key key
     * @return <field, value>
     */
    public Map<Object, Object> hGetAll(String key) {
        return srt.opsForHash().entries(key);
    }

    // List(列表)

    /**
     * 实现命令:L PUSH key value, 将一个值 value插入到列表 key的表头
     *
     * @param key   key
     * @param value value
     * @return 执行 LPUSH命令后, 列表的长度
     */
    public Long lPush(String key, String value) {
        return srt.opsForList().leftPush(key, value);
    }

    /**
     * 实现命令:L POP key, 移除并返回列表 key的头元素
     *
     * @param key key
     * @return 列表key的头元素
     */
    public String lPop(String key) {
        return srt.opsForList().leftPop(key);
    }

    /**
     * 实现命令:R PUSH key value, 将一个值 value插入到列表 key的表尾(最右边)
     *
     * @param key   key
     * @param value value
     * @return 执行 LPUSH命令后, 列表的长度
     */
    public Long rPush(String key, String value) {
        return srt.opsForList().rightPush(key, value);
    }

    // Object

    /**
     * 获得缓存的基本对象
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key key
     */
    public void deleteObject(final String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return delete true or false
     */
    public boolean deleteObject(final Collection<String> collection) {
        Long deleted = redisTemplate.delete(collection);
        return deleted != null && deleted > 0;
    }

    /**
     * 缓存基本的对象, Integer, String, 实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象, Integer, String, 实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 调用 lua 脚本
     *
     * @param rs    RedisScript
     * @param key   key
     * @param value value
     * @param <T>   返回类型
     */
    public <T> void execute(RedisScript<T> rs, String key, String value) {
        srt.execute(rs, Collections.singletonList(key), value);
    }

    /**
     * 调用 lua 脚本
     *
     * @param rs    RedisScript
     * @param key   key(多个)
     * @param value value(多个)
     * @param <T>   返回类型
     */
    public <T> T execute(RedisScript<T> rs, List<String> key, Object... value) {
        return srt.execute(rs, key, value);
    }
}
