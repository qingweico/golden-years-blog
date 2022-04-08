package cn.qingweico.article.html;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zqw
 * @date 2021/9/14
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = {"cn.qingweico", "org.n3r.idworker"})
public class Application {
   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }
}
