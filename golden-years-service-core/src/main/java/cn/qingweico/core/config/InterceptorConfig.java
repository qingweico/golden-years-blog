package cn.qingweico.core.config;

import cn.qingweico.core.interceptor.*;
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
    public ArticlePageViewsInterceptor articlePageViewsInterceptor() {
        return new ArticlePageViewsInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }


    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(smsInterceptor())
                .addPathPatterns("/u/auth/getSmsCode");
        registry.addInterceptor(articlePageViewsInterceptor())
                .addPathPatterns("/portal/article/incPagViews");
    }
}
