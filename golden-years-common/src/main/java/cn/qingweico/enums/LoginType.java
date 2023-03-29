package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 用户可以使用的登录类型
 *
 * @author zqw
 * @date 2022/4/11
 */
@AllArgsConstructor
public enum LoginType {
    /**
     * 密码登录
     */
    PASSWORD(1, "password"),
    /**
     * github登录
     */
    GITHUB(2, "github"),
    /**
     * qq登录
     */
    QQ(3, "qq"),
    /**
     * wechat登录
     */
    WECHAT(4, "wechat"),
    /**
     * weibo登录
     */
    WEIBO(5, "weibo");


    public final Integer type;
    public final String value;
}
