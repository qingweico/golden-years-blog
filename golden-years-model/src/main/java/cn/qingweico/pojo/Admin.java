package cn.qingweico.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/09/06
 */
@Data
@Table(name = "t_admin")
public class Admin {
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
     * 人脸入库图片信息, 该信息保存到mongoDB的gridFS中, 为""则表示未开启人脸登录
     */
    private String faceId;

    /**
     * 性别 (0: 女 1: 男)
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
    private String lastLoginIp;

    /**
     * 最后一次登陆位置
     */
    private String lastLoginLocation;
    /**
     * 角色id
     */
    private String roleId;
    /**
     * 状态 0: 禁用  1: 启用
     */
    private int status;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}