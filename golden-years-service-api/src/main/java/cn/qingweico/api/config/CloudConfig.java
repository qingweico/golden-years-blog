package cn.qingweico.api.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Configuration
public class CloudConfig {

   @Bean
   @LoadBalanced
   public RestTemplate restTemplate() {
      return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
   }
}
