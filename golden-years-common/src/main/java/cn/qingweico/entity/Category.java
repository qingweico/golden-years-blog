package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
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
