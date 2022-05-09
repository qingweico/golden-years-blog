package cn.qingweico.enums;

/**
 * @author zqw
 * @date 2022/3/23
 */
public enum MenuType {

    /**
     * 菜单
     */
    MENU(0, "菜单");

    public final Integer type;
    public final String value;

    MenuType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
