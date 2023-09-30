package cn.qingweico.core.config.limit;

import java.lang.annotation.*;

/**
 * @author zqw
 * @date 2023/9/30
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RateLimiter {
    /**
     * 资源的名字
     *
     * @return String
     */
    String name() default "";

    /**
     * 资源的key
     *
     * @return String
     */
    String key() default "";

    /**
     * Key的prefix
     *
     * @return String
     */
    String prefix() default "";

    /**
     * 给定的时间段(s)
     *
     * @return int
     */
    int period() default 0;

    /**
     * 最多的访问限制次数
     *
     * @return int
     */
    int count() default 0;

    /**
     * 类型
     *
     * @return LimitType
     */
    LimitType limitType() default LimitType.CUSTOMER;
}
