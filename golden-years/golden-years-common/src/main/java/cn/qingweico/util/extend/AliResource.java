package cn.qingweico.util.extend;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author:qiming
 * @date: 2021/9/5
 */
@Component
@PropertySource("classpath:aliyun.properties")
@ConfigurationProperties(prefix = "aliyun")
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
