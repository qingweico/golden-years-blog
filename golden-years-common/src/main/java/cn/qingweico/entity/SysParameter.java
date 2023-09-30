package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 系统参数配置
 *
 * @author zqw
 * @date 2023/4/4
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SysParameter extends BaseEntity {
    /**
     * 系统参数编码;唯一
     */
    private String code;
    /**
     * 系统参数值
     */
    private String value;
    /**
     * 系统参数描述
     */
    private String description;
}
