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
@Table(name = "t_category_menu")
public class CategoryMenu implements Comparable<CategoryMenu> {

    @Id
    private String id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单级别(一级分类, 二级分类)
     */
    private Integer menuLevel;

    /**
     * 菜单类型(0: 菜单, 1: 按钮)
     */
    private Integer menuType;

    /**
     * 介绍
     */
    private String summary;

    /**
     * Icon图标
     */
    private String icon;

    /**
     * 父ID
     */
    private String parentId;

    /**
     * URL地址
     */
    private String url;

    /**
     * 排序字段(越大越靠前)
     */
    private Integer sort;

    /**
     * 是否显示  1: 是  0: 否
     */
    private Integer isShow;

    /**
     * 是否跳转外部URL
     */
    private Integer isJumpExternalUrl;

    /**
     * 父菜单
     */
    private CategoryMenu parentCategoryMenu;

    /**
     * 子菜单
     */
    private List<CategoryMenu> childCategoryMenu;
    /**
     * 状态 0: 禁用  1: 启用
     */
    private int status;

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
    public int compareTo(CategoryMenu o) {

        if (this.sort >= o.getSort()) {
            return -1;
        }
        return 1;
    }
}
