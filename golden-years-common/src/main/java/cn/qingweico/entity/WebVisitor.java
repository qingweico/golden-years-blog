package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * web访客(包括后台和主站)
 *
 * @author zqw
 * @date 2022/4/18
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class WebVisitor extends BaseEntity {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户IP
     */
    private String ip;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;
}
