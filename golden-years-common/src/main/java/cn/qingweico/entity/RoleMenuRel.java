package cn.qingweico.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zqw
 * @date 2023/2/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class RoleMenuRel extends BaseEntity {
    /**
     * 角色id
     */
    private String roleId;
    /**
     * 菜单id
     */
    private String menuId;
}
