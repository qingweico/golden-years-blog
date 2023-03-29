package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 用户状态:
 * 0: 未激活
 * 1: 已激活: 基本信息是否完善,真实姓名,邮箱地址,性别,生日,住址等
 * 如果没有完善,则用户不能发表评论,不能点赞,不能关注
 * 2: 已冻结
 *
 * @author zqw
 * @date 2021/9/6
 */
@AllArgsConstructor
public enum UserStatus {
    /**
     * 未激活
     */
    INACTIVE(0, "未激活"),
    /**
     * 已激活
     */
    ACTIVE(1, "已激活"),
    /**
     * 已冻结
     */
    FROZEN(2, "已冻结");

    public final Integer type;
    public final String value;

    /**
     * 判断传入的用户状态是不是有效的值
     *
     * @param val 用户状态
     * @return boolean -> 用户状态是否有效
     */
    public static boolean isUserStatusValid(Integer val) {
        if (val != null) {
            return val.equals(INACTIVE.type)
                    || val.equals(ACTIVE.type)
                    || val.equals(FROZEN.type);
        }
        return false;
    }
}
