package cn.qingweico.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户-角色中间表
 *
 * @author zqw
 * @date 2023/2/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserRoleRel extends BaseEntity {
    /**
     * SysUserId
     */
    private String sysUserId;
    /**
     * 角色id
     */
    private String roleId;
}
