package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 主站用户
 *
 * @author zqw
 * @date 2021/09/06
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class User extends BaseEntity {
    private static final long serialVersionUID = -53242415098753974L;
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
