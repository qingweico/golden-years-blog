package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zqw
 * @date 2021/9/6
 */
@AllArgsConstructor
@Getter
public enum UserStatus {
    /**
     * 禁用
     */
    DISABLE(0, "禁用"),
    /**
     * 可用
     */
    AVAILABLE(1, "可用");

    private final Integer val;
    private final String desc;

    /**
     * 判断传入的用户状态是不是有效的值
     *
     * @param val 用户状态
     * @return boolean -> 用户状态是否有效
     */
    public static boolean isUserStatusValid(Integer val) {
        if (val != null) {
            return val.equals(DISABLE.val)
                    || val.equals(AVAILABLE.val);
        }
        return false;
    }
}
