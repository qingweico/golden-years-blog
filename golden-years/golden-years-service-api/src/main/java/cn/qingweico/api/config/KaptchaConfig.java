package cn.qingweico.api.config;

import com.google.code.kaptcha.servlet.KaptchaServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author:qiming
 * @date: 2021/9/23
 */
@Configuration
public class KaptchaConfig {
    /**
     * 验证码
     *
     * @return ServletRegistrationBean
     */
    @Bean
    public ServletRegistrationBean<KaptchaServlet> servletRegistrationBean() {
        ServletRegistrationBean<KaptchaServlet> servletRegistrationBean = new ServletRegistrationBean<>(new KaptchaServlet(), "/Kaptcha");
        servletRegistrationBean.addInitParameter("kaptcha.border", "no");
        servletRegistrationBean.addInitParameter("kaptcha.textproducer.font.color", "black");
        servletRegistrationBean.addInitParameter("kaptcha.textproducer.font.size", "40");
        servletRegistrationBean.addInitParameter("kaptcha.image.width", "130");
        servletRegistrationBean.addInitParameter("kaptcha.image.height", "40");
        servletRegistrationBean.addInitParameter("kaptcha.testproducer.char.string", "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
        servletRegistrationBean.addInitParameter("kaptcha.textproducer.char.length", "4");
        servletRegistrationBean.addInitParameter("kaptcha.noise.color", "black");
        servletRegistrationBean.addInitParameter("kaptcha.textproducer.font.names", "Courier");
        return servletRegistrationBean;

    }
}
