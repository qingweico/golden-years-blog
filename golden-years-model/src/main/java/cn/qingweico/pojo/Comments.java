package cn.qingweico.pojo;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;


/**
 * @author zqw
 * @date 2021/9/13
 */

@Data
public class Comments {
    @Id
    private String id;

    /**
     * 评论的文章与文章作者的关联id
     */
    private String writerId;

    /**
     * 如果是回复留言, 则本条为子留言, 需要关联查询
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
     * 发布留言的用户id
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
     * 留言内容
     */
    private String content;

    /**
     * 留言时间
     */
    private Date createTime;
}