package cn.qingweico.files.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author:qiming
 * @date: 2021/9/22
 */
@Component
@PropertySource("classpath:file-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "file")
public class FileResource {
   private String fsHost;
   private String endpoint;
   private String bucketName;
   private String objectName;
   private String ossHost;

   public String getFsHost() {
      return fsHost;
   }

   public void setFsHost(String fsHost) {
      this.fsHost = fsHost;
   }

   public String getEndpoint() {
      return endpoint;
   }

   public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
   }

   public String getBucketName() {
      return bucketName;
   }

   public void setBucketName(String bucketName) {
      this.bucketName = bucketName;
   }

   public String getObjectName() {
      return objectName;
   }

   public void setObjectName(String objectName) {
      this.objectName = objectName;
   }

   public String getOssHost() {
      return ossHost;
   }

   public void setOssHost(String ossHost) {
      this.ossHost = ossHost;
   }
}
