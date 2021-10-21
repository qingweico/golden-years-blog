package cn.qingweico.api.config;

import cn.qingweico.api.interceptor.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author:qiming
 * @date: 2021/9/6
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor() {
        return new PassportInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    @Bean
    public UserActiveInterceptor userActiveInterceptor() {
        return new UserActiveInterceptor();
    }

    @Bean
    public AdminTokenInterceptor adminTokenInterceptor() {
        return new AdminTokenInterceptor();
    }

    @Bean
    public ArticleReadInterceptor articleReadInterceptor() {
        return new ArticleReadInterceptor();
    }


    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getSmsCode");

        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/user/getAccountInfo")
                .addPathPatterns("/user/updateUserInfo")
                .addPathPatterns("/fs/uploadFace")
                .addPathPatterns("/fs/uploadSomeFiles");
//                .addPathPatterns("/fans/follow")
//                .addPathPatterns("/fans/unfollow");

        registry.addInterceptor(userActiveInterceptor())
                .addPathPatterns("fs/uploadSomeFiles");
//                .addPathPatterns("/fans/follow")
//                .addPathPatterns("/fans/unfollow");

        registry.addInterceptor(adminTokenInterceptor())
                .addPathPatterns("/admin/adminIsExist")
                .addPathPatterns("/admin/addNewAdmin")
                .addPathPatterns("/admin/getAdminList")
                .addPathPatterns("/fs/uploadToGridFS")
                .addPathPatterns("/fs/readInGridFS")
                .addPathPatterns("/friendLink/saveOrUpdateFriendLink")
                .addPathPatterns("/friendLink/getFriendLinkList")
                .addPathPatterns("/friendLink/delete");

        registry.addInterceptor(articleReadInterceptor())
                .addPathPatterns("/portal/article/readArticle");



    }


}
