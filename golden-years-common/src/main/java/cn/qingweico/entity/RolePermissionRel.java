package cn.qingweico.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色-权限中间表
 *
 * @author zqw
 * @date 2023/2/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class RolePermissionRel extends BaseEntity {

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 权限id
     */
    private String permId;
}
