package cn.qingweico.util.aliyun;

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
public class AliResource {

   private String accessKeyId;
   private String accessKeySecret;

   public String getAccessKeyId() {
      return accessKeyId;
   }

   public void setAccessKeyId(String accessKeyId) {
      this.accessKeyId = accessKeyId;
   }

   public String getAccessKeySecret() {
      return accessKeySecret;
   }

   public void setAccessKeySecret(String accessKeySecret) {
      this.accessKeySecret = accessKeySecret;
   }
}
