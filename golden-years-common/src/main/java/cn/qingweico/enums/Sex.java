package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 用户性别
 *
 * @author zqw
 * @date 2021/9/6
 */
@AllArgsConstructor
public enum Sex {
    /**
     * 女
     */
    WOMAN(0, "女"),
    /**
     * 男
     */
    MAN(1, "男"),
    /**
     * 保密
     */
    SECRET(2, "保密");

    public final Integer type;
    public final String value;
}
