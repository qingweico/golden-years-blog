package cn.qingweico.core.config.limit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zqw
 * @date 2023/9/30
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix="gy.rate-limit")
public class RateLimiterProperties {
    private boolean enable;
    private int period;
    private int count;
}
