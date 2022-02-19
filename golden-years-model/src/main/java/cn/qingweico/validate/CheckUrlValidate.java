package cn.qingweico.validate;

import cn.qingweico.util.CheckUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author zqw
 * @date 2021/9/10
 */
public class CheckUrlValidate implements ConstraintValidator<CheckUrl, String> {

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        return CheckUtils.checkUrl(url.trim());
    }
}
