package cn.qingweico.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author zqw
 * @date 2021/9/5
 */
@SpringBootApplication
@EnableOpenApi
@EnableAsync
@MapperScan(basePackages = "cn.qingweico.user.mapper")
@EnableFeignClients
@ComponentScan(basePackages = {"cn.qingweico"})
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
