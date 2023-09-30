package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 主站配置
 *
 * @author zqw
 * @date 2022/4/11
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class WebConfig extends BaseEntity {

    private static final long serialVersionUID = 2722316933931523496L;
    /**
     * 网站logo
     */
    private String logo;

    /**
     * 网站名称
     */
    private String name;

    /**
     * 网站介绍
     */
    private String description;

    /**
     * 网站作者
     */
    private String author;
    /**
     * 网站备案号
     */
    private String recordNum;

    /**
     * 网站标题
     */
    private String title;
}
