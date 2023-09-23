package cn.qingweico.entity;

import lombok.*;


/**
 * 粉丝
 *
 * @author zqw
 * @date 2021/9/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fans extends BaseEntity {
    /**
     * 关注的作者
     */
    private String authorId;

    /**
     * 粉丝用户id
     */
    private String fanId;
}
