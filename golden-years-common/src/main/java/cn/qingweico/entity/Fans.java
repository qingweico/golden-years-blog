package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;


/**
 * 粉丝
 *
 * @author zqw
 * @date 2021/9/13
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Fans extends BaseEntity {
    private static final long serialVersionUID = -135137388555108580L;
    /**
     * 关注的作者
     */
    private String authorId;

    /**
     * 粉丝用户id
     */
    private String fanId;
}
