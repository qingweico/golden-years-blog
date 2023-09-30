package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 用户-角色中间表
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUserRoleRel extends BaseEntity {
    private static final long serialVersionUID = 1859596325333985211L;
    /**
     * SysUserId
     */
    private String sysUserId;
    /**
     * 角色id
     */
    private String roleId;
}
