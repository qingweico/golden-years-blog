package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 角色-权限中间表
 *
 * @author zqw
 * @date 2023/2/19
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
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
