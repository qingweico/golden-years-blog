package cn.qingweico.pojo;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;


/**
 * @author zqw
 * @date 2021/9/13
 */

@Data
@Table(name = "t_comment")
public class Comments {
    @Id
    private String id;

    /**
     * 评论的文章与文章作者的关联id
     */
    private String author;

    /**
     * 如果是回复评论, 则本条为子评论, 需要关联查询
     */
    private String fatherId;

    /**
     * 回复的那个文章id
     */
    private String articleId;

    /**
     * 冗余文章标题, 宽表处理, 非规范化的sql思维
     */
    private String articleTitle;

    /**
     * 文章封面
     */
    private String articleCover;

    /**
     * 发布评论的用户id
     */
    private String commentUserId;

    /**
     * 冗余用户昵称, 非一致性字段, 用户修改昵称后可以不用同步
     */
    private String commentUserNickname;

    /**
     * 冗余的用户头像
     */
    private String commentUserFace;

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

    /**
     * 留言时间
     */
    private Date createTime;
}