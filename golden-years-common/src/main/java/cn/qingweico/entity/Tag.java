package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 文章标签表
 *
 * @author zqw
 * @date 2022/3/3
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Tag extends BaseEntity {
    /**
     * 标签名称
     */
    private String name;
    /**
     * 标签状态
     */
    private Integer status;
    /**
     * 标签颜色
     */
    private String color;

    /**
     * 个人自定义标签 - 标签用户id标识
     */
    private String userId;
    /**
     * 系统标签 - 默认为1; 否则为用户自定义标签(0)
     */
    private Integer sys;
}
