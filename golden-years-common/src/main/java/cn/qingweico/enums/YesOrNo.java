package cn.qingweico.enums;

/**
 * 是否
 *
 * @author zqw
 * @date 2021/9/11
 */
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

    YesOrNo(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
