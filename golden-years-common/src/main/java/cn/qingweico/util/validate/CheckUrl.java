package cn.qingweico.util.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义校验注解
 *
 * @author zqw
 * @date 2021/9/10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckUrlValidate.class)
public @interface CheckUrl {
   /// 满足 JSR303 规范
   String message() default "Url不正确";
   Class<?>[] groups() default {};
   Class<? extends Payload>[] payload() default {};
}
