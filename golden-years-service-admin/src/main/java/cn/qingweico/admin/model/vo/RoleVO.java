package cn.qingweico.admin.model.vo;

import lombok.Data;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2022/3/23
 */
@Data
public class RoleVO {
    /**
     * id
     */
    private String id;

    private Integer status;
    /**
     * 角色名称
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String roleName;

    /**
     * 介绍
     */
    private String summary;

    /**
     * 该角色所能管辖的菜单
     */
    private String categoryMenuIds;
}
