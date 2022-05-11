package cn.qingweico.files.resource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author zqw
 * @date 2021/9/22
 */
@Component
@PropertySource("classpath:file-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "file")
@Data
public class FileResource {
   private String fsHost;
   private String endpoint;
   private String bucketName;
   private String objectName;
   private String ossHost;
}
