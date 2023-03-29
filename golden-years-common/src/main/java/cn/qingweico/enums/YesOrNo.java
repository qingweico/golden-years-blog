package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 是否
 *
 * @author zqw
 * @date 2021/9/11
 */
@AllArgsConstructor
public enum YesOrNo {
    /**
     * 否
     */
    NO(0, "否"),
    /**
     * 是
     */
    YES(1, "是");

    public final Integer type;
    public final String value;
}
