package cn.qingweico.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/09/06
 */
@Data
@Table(name = "t_admin")
public class AdminUser {
    @Id
    private String id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 人脸入库图片信息, 该信息保存到mongoDB的gridFS中
     */
    private String faceId;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 最后一次登陆时间
     */
    private Date lastLoginTime;
    /**
     * 最后一次登陆ip
     */
    private Date lastLoginIp;
    /**
     * 状态
     */
    private Integer status;
    /**
     * QQ
     */
    private String qqNumber;
    /**
     * 微信
     */
    private String wechat;
    /**
     * github地址
     */
    private String github;
    /**
     * 角色id
     */
    private String roleId;
}