package cn.qingweico.pojo;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

/**
 * @author zqw
 * @date 2021/09/06
 */
@Data
@Table(name = "t_user")
public class User {
    @Id
    private String id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String face;

    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 用户密码(默认密码为123456)
     */
    private String password;
    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 性别 1:男  0:女  2:保密
     */
    private Integer sex;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 用户状态：
     * 0: 未激活
     * 1: 已激活 基本信息是否完善, 真实姓名、邮箱地址、性别、生日、住址等, 如果没有完善, 则用户不能在创作中心操作, 不能关注
     * 2: 已冻结
     */
    private Integer activeStatus;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}