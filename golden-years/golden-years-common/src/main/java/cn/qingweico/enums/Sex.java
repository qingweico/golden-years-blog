package cn.qingweico.enums;

/**
 * @author:qiming
 * @date: 2021/9/6
 */
public enum Sex {
    WOMAN(0, "女"),
    MAN(1, "男"),
    SECRET(2, "保密");

    public final Integer type;
    public final String value;

    Sex(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
