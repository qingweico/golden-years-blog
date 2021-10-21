package cn.qingweico.pojo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author:qiming
 * @date: 2021/9/22
 */

@Table(name = "user_login_log")
public class UserLoginLog {
    @Id
    private String id;

    /**
     * 登陆用户名
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 登陆位置
     */
    @Column(name = "login_location")
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
    @Column(name = "login_time")
    private Date loginTime;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取登陆用户名
     *
     * @return user_id - 登陆用户名
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置登陆用户名
     *
     * @param userId 登陆用户名
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取登陆位置
     *
     * @return login_location - 登陆位置
     */
    public String getLoginLocation() {
        return loginLocation;
    }

    /**
     * 设置登陆位置
     *
     * @param loginLocation 登陆位置
     */
    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    /**
     * 获取登陆ip
     *
     * @return ipaddr - 登陆ip
     */
    public String getIpaddr() {
        return ipaddr;
    }

    /**
     * 设置登陆ip
     *
     * @param ipaddr 登陆ip
     */
    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    /**
     * 获取浏览器类型
     *
     * @return browser - 浏览器类型
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * 设置浏览器类型
     *
     * @param browser 浏览器类型
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * 获取操作系统类型
     *
     * @return os - 操作系统类型
     */
    public String getOs() {
        return os;
    }

    /**
     * 设置操作系统类型
     *
     * @param os 操作系统类型
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * 获取登陆时间
     *
     * @return login_time - 登陆时间
     */
    public Date getLoginTime() {
        return loginTime;
    }

    /**
     * 设置登陆时间
     *
     * @param loginTime 登陆时间
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}