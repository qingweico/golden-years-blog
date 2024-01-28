package cn.qingweico.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 系统菜单
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
@Accessors(chain = true)
public class SysMenu extends BaseEntity {
    private static final long serialVersionUID = -4469622809393783319L;
    /**
     * 菜单名称
     */
    private String name;

    /**
     * 类型(M目录 C菜单 F按钮)
     */
    private String type;
    /**
     * 菜单描述
     */
    private String desc;

    /**
     * icon图标
     */
    private String icon;

    /**
     * 父ID
     */
    private String parentId;

    /**
     * 父菜单名称
     */
    @TableField(exist = false)
    private String parentName;

    /** 组件路径 */
    private String component;

    /**
     * path
     */
    private String path;

    /**
     * 排序字段(越大越靠前)
     */
    private Integer sequence;

    /**
     * 是否隐藏  1: 是  0: 否
     */
    private Integer hidden;

    /** 是否为外链(0是 1否) */
    private String isFrame;
    /**
     * 是否缓存(0缓存 1不缓存)
     */
    private String isCache;

    /**
     * 子菜单
     */
    private List<SysMenu> children;


    /**
     * 路由参数
     */
    private String query;
    /**
     * 权限字符串
     */
    private String perms;


}
