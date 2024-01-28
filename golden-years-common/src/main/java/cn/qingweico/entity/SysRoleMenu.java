package cn.qingweico.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author zqw
 * @date 2023/10/1
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("gy_sys_role_menu")
@Accessors(chain = true)
public class SysRoleMenu extends BaseEntity {
    private static final long serialVersionUID = -8150879462412894156L;
    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;

}
