package cn.qingweico.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2022/3/23
 */
@Data
@Table(name = "t_sys_menu")
public class SysMenu implements Comparable<SysMenu> {

    @Id
    private String id;

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
     * 介绍
     */
    private String summary;

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
    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Override
    public int compareTo(SysMenu o) {

        if (this.sort >= o.getSort()) {
            return -1;
        }
        return 1;
    }
}
