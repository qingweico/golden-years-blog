package cn.qingweico.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色相关
 *
 * @author zqw
 * @date 2022/3/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Role extends BaseEntity {

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
