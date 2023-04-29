package cn.qingweico.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author zqw
 * @date 2021/9/13
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Comments extends BaseEntity {
    /**
     * 文章作者id
     */
    private String authorId;

    /**
     * 如果是回复评论, 则本条为子评论
     */
    private String parentId;

    /**
     * 文章id
     */
    private String articleId;

    /**
     * 发布评论的用户id
     */
    private String userId;

    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论点赞数
     */
    private Integer likes;
    /**
     * 评论图片
     */
    private String imgSrc;
}
