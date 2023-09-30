package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zqw
 * @date 2022/3/23
 */
@AllArgsConstructor
@Getter
public enum MenuType {

    /**
     * 菜单
     */
    MENU(0, "菜单");

    private final Integer val;
    private final String desc;
}
