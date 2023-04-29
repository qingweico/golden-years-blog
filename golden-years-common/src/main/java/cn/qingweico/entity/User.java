package cn.qingweico.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class User extends BaseEntity {
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 账号
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
     * 用户密码
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
     * 用户状态：
     * 0: 未激活
     * 1: 已激活 基本信息是否完善, 真实姓名如果没有完善, 则用户不能在创作中心操作, 不能关注
     * 2: 已冻结
     */
    private Integer activeStatus;
}
