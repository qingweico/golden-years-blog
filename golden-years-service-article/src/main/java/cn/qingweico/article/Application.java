package cn.qingweico.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zqw
 * @date 2021/9/11
 */
@SpringBootApplication
@MapperScan(basePackages = {"cn.qingweico.article.mapper"})
@ComponentScan(basePackages = {"cn.qingweico"})
@EnableFeignClients
public class Application {
   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }
}
