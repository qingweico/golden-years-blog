package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户性别
 *
 * @author zqw
 * @date 2021/9/6
 */
@AllArgsConstructor
@Getter
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

    private final Integer val;
    private final String desc;
}
