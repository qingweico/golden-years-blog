package cn.qingweico.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author zqw
 * @date 2021/9/10
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.qingweico.admin.mapper")
@ComponentScan(basePackages = {"cn.qingweico", "org.n3r.idworker"})
@EnableFeignClients
public class Application {
   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }
}
