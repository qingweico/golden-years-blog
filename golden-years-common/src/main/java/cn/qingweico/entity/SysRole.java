package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;

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

    /**
     * 状态 0: 禁用  1: 启用
     */
    private int status;
}
