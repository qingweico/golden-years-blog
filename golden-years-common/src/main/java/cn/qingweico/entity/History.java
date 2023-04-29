package cn.qingweico.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户浏览历史
 *
 * @author zqw
 * @date 2022/4/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class History extends BaseEntity {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 文章id
     */
    private String articleId;
}
