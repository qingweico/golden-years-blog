package cn.qingweico.util.aliyun;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author zqw
 * @date 2021/9/5
 */
@Component
@PropertySource("classpath:ali.properties")
@ConfigurationProperties(prefix = "ali")
@Data
public class AliResource {
   private String accessKeyId;
   private String accessKeySecret;
}
