package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否
 *
 * @author zqw
 * @date 2021/9/11
 */
@AllArgsConstructor
@Getter
public enum YesOrNo {
    /**
     * 否
     */
    NO(0, "否"),
    /**
     * 是
     */
    YES(1, "是");

    private final Integer val;
    private final String desc;
}
