package cn.qingweico.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 *
 * @author zqw
 * @date 2020/11/15
 */

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * 页面跨域访问Controller过滤
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("POST", "GET", "DELETE", "PUT")
                .allowedOrigins("*");
    }
}