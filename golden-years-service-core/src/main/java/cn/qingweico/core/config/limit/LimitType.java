package cn.qingweico.core.config.limit;

/**
 * 限流方式
 *
 * @author zqw
 * @date 2023/9/30
 */
public enum LimitType {

    /**
     * 自定义key
     */
    CUSTOMER,
    /**
     * 请求IP
     */
    IP,
    /**
     * 请求uri
     */
    URI
}
