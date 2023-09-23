package cn.qingweico.core.config;

import cn.qingweico.util.pool.ThreadPoolBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

/**
 * 线程池配置
 *
 * @author zqw
 * @date 2023/6/8
 */
@Configuration
public class CustomizableThreadPoolConfig {
    @Bean
    public ExecutorService customizableThreadPool() {
        return ThreadPoolBuilder.builder(100)
                .corePoolSize(10)
                .maxPoolSize(100)
                .preStartAllCore(true)
                .allowCoreThreadTimeOut(true)
                .build();
    }
}
