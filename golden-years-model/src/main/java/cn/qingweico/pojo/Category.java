package cn.qingweico.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Data
@Table(name = "t_category")
public class Category {
    @Id
    private String id;

    /**
     * 文章分类名称
     */
    private String name;

    /**
     * 类别描述
     */
    private String description;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 分类状态(0: 不可用 1: 可用)
     */
    private Integer status;

}