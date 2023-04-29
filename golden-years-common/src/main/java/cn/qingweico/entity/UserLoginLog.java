package cn.qingweico.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 主站用户登录日志
 *
 * @author zqw
 * @date 2021/9/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class UserLoginLog extends BaseEntity {

    /**
     * 登陆用户名
     */
    private String userId;

    /**
     * 登陆位置
     */
    private String loginLocation;

    /**
     * 登陆ip
     */
    private String ipaddr;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统类型
     */
    private String os;

    /**
     * 登陆时间
     */
    private Date loginTime;
}
