package cn.qingweico.api.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author:qiming
 * @date: 2021/9/5
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

//    http://localhost:8001/swagger-ui.html     原路径
//    http://localhost:8001/doc.html            新路径

    /**
     * 配置swagger2核心配置 docket
     * @return Docket
     */
    @Bean
    public Docket createRestApi() {

        Predicate<RequestHandler> userPredicate = RequestHandlerSelectors.basePackage("cn.qingweico.user.controller");
        Predicate<RequestHandler> adminPredicate = RequestHandlerSelectors.basePackage("cn.qingweico.admin.controller");
        Predicate<RequestHandler> filesPredicate = RequestHandlerSelectors.basePackage("cn.qingweico.files.controller");
        Predicate<RequestHandler> articlePredicate = RequestHandlerSelectors.basePackage("cn.qingweico.article.controller");


        //指定api类型为swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                // 用于定义api文档汇总信息
                .apiInfo(apiInfo())
                .select()
                .apis(Predicates.or(userPredicate, adminPredicate, filesPredicate, articlePredicate))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 文档页标题
                .title("流金岁月")
                // 联系人信息
                .contact(new Contact("qiming",
                        "https://golden.qingweico.cn",
                        "17796706221@163.com"))
                // 详细信息
                .description("")
                // 文档版本号
                .version("1.0.1")
                // 网站地址
                .termsOfServiceUrl("https://golden.qingweico.cn")
                .build();
    }

}
