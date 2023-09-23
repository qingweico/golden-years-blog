package cn.qingweico.entity;

import lombok.*;

import java.util.Date;

/**
 * 主站用户
 *
 * @author zqw
 * @date 2021/09/06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 用户名(唯一)
     */
    private String username;

    /**
     * 头像
     */
    private String face;

    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 用户账号
     */
    private String account;

    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户昵称
     */
    private String nickname;
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
     * 用户状态
     * 0: 禁用
     * 1: 可用
     */
    private Integer available;
}
