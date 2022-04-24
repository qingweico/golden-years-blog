package cn.qingweico.enums;

/**
 * @author zqw
 * @date 2022/4/15
 */
public enum FavoritesType {
    /**
     * 公开
     */
    PUBLIC(1, "公开"),
    /**
     * 私密
     */
    SECRET(0, "私密");
    public final Integer type;
    public final String value;

    FavoritesType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
