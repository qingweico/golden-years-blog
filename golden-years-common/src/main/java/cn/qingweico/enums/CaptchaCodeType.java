package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码类型
 *
 * @author zqw
 * @date 2023/9/23
 */
@AllArgsConstructor
@Getter
public enum CaptchaCodeType {

    /**
     * 数学
     */
    MATH("math", "数学"),
    /**
     * 字符
     */
    CHAR("char", "字符");
    public final String val;
    public final String desc;
}
