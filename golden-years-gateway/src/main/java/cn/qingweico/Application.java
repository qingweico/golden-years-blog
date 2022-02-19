package cn.qingweico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author zqw
 * @date 2021/10/15
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Application {
   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }
}
