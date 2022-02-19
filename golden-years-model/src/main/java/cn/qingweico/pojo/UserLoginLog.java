package cn.qingweico.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/22
 */

@Data
public class UserLoginLog {
    @Id
    private String id;

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