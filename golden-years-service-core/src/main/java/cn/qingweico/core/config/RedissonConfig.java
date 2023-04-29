package cn.qingweico.core.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Configuration;

/**
 * @author zqw
 * @date 2022/8/30
 */
@Configuration
public class RedissonConfig {

    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://106.12.136.221")
                .setPassword("990712");
        return Redisson.create(config);
    }
}
