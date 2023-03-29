package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * @author zqw
 * @date 2022/3/23
 */
@AllArgsConstructor
public enum MenuType {

    /**
     * 菜单
     */
    MENU(0, "菜单");

    public final Integer type;
    public final String value;
}
