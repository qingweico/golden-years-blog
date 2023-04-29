package cn.qingweico.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author zqw
 * @date 2022/8/26
 */
@Component
public class ApplicationContextFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        ApplicationContextFactory.applicationContext = applicationContext;
    }

    public static Object getBean(Class<?> className){
        return applicationContext.getBean(className);
    }

    public static Object getBean(String beanName){
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(String beanName,Class<T> t){
        Object bean = applicationContext.getBean(beanName);

        if(bean != null && t.isAssignableFrom(bean.getClass()) ){
            return (T)bean;
        }

        return null;
    }

    /**
     * 获取应用名称
     * @return application name
     */
    public static String getApplicationName(){
        return applicationContext.getEnvironment().getProperty("spring.application.name");
    }


    // 国际化使用

    public static String getMessage(String key) {
        return applicationContext.getMessage(key, null, Locale.getDefault());
    }


    /// 获取当前环境

    public static String getActiveProfile() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }
}
