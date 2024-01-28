package cn.qingweico.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 角色相关
 *
 * @author zqw
 * @date 2022/3/23
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gy_role")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = -5308132468007786411L;
    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /** 菜单树选择项是否关联显示(0 父子不互相关联显示 1 父子互相关联显示) */
    private boolean menuCheckStrictly;


    /** 角色菜单权限 */
    @TableField(exist = false)
    private Set<String> permissions;

}
