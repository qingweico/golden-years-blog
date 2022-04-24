package cn.qingweico.api.config;

import cn.qingweico.api.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.constraints.NotNull;

/**
 * @author zqw
 * @date 2021/9/6
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public SmsInterceptor smsInterceptor() {
        return new SmsInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }


    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(smsInterceptor())
                .addPathPatterns("/auth/getSmsCode");
    }
}
