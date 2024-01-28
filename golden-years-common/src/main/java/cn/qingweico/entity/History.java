package cn.qingweico.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户浏览历史
 *
 * @author zqw
 * @date 2022/4/15
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class History extends BaseEntity {

    private static final long serialVersionUID = -2819567046658213973L;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 文章id
     */
    private String articleId;
}
