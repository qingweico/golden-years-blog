package cn.qingweico.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author zqw
 * @date 2021/9/10
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.qingweico.admin.mapper")
@ComponentScan(basePackages = {"cn.qingweico"})
@EnableFeignClients
@EnableAsync
public class AdminServiceApplication {
   public static void main(String[] args) {
      SpringApplication.run(AdminServiceApplication.class, args);
   }
}
