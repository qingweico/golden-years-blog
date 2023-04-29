package cn.qingweico.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zqw
 * @date 2021/9/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Category extends BaseEntity {

    /**
     * 文章分类名称
     */
    private String name;

    /**
     * 类别描述
     */
    private String description;

    /**
     * 分类状态(0: 不可用 1: 可用)
     */
    private Integer status;

}
