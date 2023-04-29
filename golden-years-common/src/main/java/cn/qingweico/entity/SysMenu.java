package cn.qingweico.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统菜单
 *
 * @author zqw
 * @date 2022/3/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class SysMenu extends BaseEntity implements Comparable<SysMenu> {
    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单级别(一级分类, 二级分类)
     */
    private Integer level;

    /**
     * 菜单类型(0: 菜单, 1: 按钮)
     */
    private Integer type;

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
     * path
     */
    private String path;

    /**
     * 排序字段(越大越靠前)
     */
    private Integer sort;

    /**
     * 是否隐藏  1: 是  0: 否
     */
    private Integer hidden;

    /**
     * 是否跳转外部URL
     */
    private Integer external;

    /**
     * 父菜单
     */
    private SysMenu parent;

    /**
     * 子菜单
     */
    private List<SysMenu> children;
    /**
     * 状态 0: 禁用  1: 启用
     */
    private int status;

    @Override
    public int compareTo(SysMenu o) {

        if (this.sort > o.getSort()) {
            return -1;
        }else if(this.sort.equals(o.getSort())) {
            return 0;
        }
        return 1;
    }
}
