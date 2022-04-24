package cn.qingweico.enums;

/**
 * @author zqw
 * @date 2022/4/11
 */
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

    LoginType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
