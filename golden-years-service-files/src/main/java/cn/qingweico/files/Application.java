package cn.qingweico.files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author:qiming
 * @date: 2021/9/8
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"cn.qingweico", "org.n3r.idworker"})
public class Application {
   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }
}