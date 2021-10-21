package cn.qingweico.pojo;

import java.util.Date;
import javax.persistence.*;


/**
 * @author:qiming
 * @date: 2021/9/13
 */


public class Comments {
    @Id
    private String id;

    /**
     * 评论的文章是哪个作者的关联id
     */
    @Column(name = "writer_id")
    private String writerId;

    /**
     * 如果是回复留言，则本条为子留言，需要关联查询
     */
    @Column(name = "father_id")
    private String fatherId;

    /**
     * 回复的那个文章id
     */
    @Column(name = "article_id")
    private String articleId;

    /**
     * 冗余文章标题，宽表处理，非规范化的sql思维，对于几百万文章和几百万评论的关联查询来讲，性能肯定不行，所以做宽表处理，从业务角度来说，文章发布以后不能随便修改标题和封面的
     */
    @Column(name = "article_title")
    private String articleTitle;

    /**
     * 文章封面
     */
    @Column(name = "article_cover")
    private String articleCover;

    /**
     * 发布留言的用户id
     */
    @Column(name = "comment_user_id")
    private String commentUserId;

    /**
     * 冗余用户昵称，非一致性字段，用户修改昵称后可以不用同步
     */
    @Column(name = "comment_user_nickname")
    private String commentUserNickname;

    /**
     * 冗余的用户头像
     */
    @Column(name = "comment_user_face")
    private String commentUserFace;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 留言时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取评论的文章是哪个作者的关联id
     *
     * @return writer_id - 评论的文章是哪个作者的关联id
     */
    public String getWriterId() {
        return writerId;
    }

    /**
     * 设置评论的文章是哪个作者的关联id
     *
     * @param writerId 评论的文章是哪个作者的关联id
     */
    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    /**
     * 获取如果是回复留言，则本条为子留言，需要关联查询
     *
     * @return father_id - 如果是回复留言，则本条为子留言，需要关联查询
     */
    public String getFatherId() {
        return fatherId;
    }

    /**
     * 设置如果是回复留言，则本条为子留言，需要关联查询
     *
     * @param fatherId 如果是回复留言，则本条为子留言，需要关联查询
     */
    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    /**
     * 获取回复的那个文章id
     *
     * @return article_id - 回复的那个文章id
     */
    public String getArticleId() {
        return articleId;
    }

    /**
     * 设置回复的那个文章id
     *
     * @param articleId 回复的那个文章id
     */
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    /**
     * 获取冗余文章标题，宽表处理，非规范化的sql思维，对于几百万文章和几百万评论的关联查询来讲，性能肯定不行，所以做宽表处理，从业务角度来说，文章发布以后不能随便修改标题和封面的
     *
     * @return article_title - 冗余文章标题，宽表处理，非规范化的sql思维，对于几百万文章和几百万评论的关联查询来讲，性能肯定不行，所以做宽表处理，从业务角度来说，文章发布以后不能随便修改标题和封面的
     */
    public String getArticleTitle() {
        return articleTitle;
    }

    /**
     * 设置冗余文章标题，宽表处理，非规范化的sql思维，对于几百万文章和几百万评论的关联查询来讲，性能肯定不行，所以做宽表处理，从业务角度来说，文章发布以后不能随便修改标题和封面的
     *
     * @param articleTitle 冗余文章标题，宽表处理，非规范化的sql思维，对于几百万文章和几百万评论的关联查询来讲，性能肯定不行，所以做宽表处理，从业务角度来说，文章发布以后不能随便修改标题和封面的
     */
    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    /**
     * 获取文章封面
     *
     * @return article_cover - 文章封面
     */
    public String getArticleCover() {
        return articleCover;
    }

    /**
     * 设置文章封面
     *
     * @param articleCover 文章封面
     */
    public void setArticleCover(String articleCover) {
        this.articleCover = articleCover;
    }

    /**
     * 获取发布留言的用户id
     *
     * @return comment_user_id - 发布留言的用户id
     */
    public String getCommentUserId() {
        return commentUserId;
    }

    /**
     * 设置发布留言的用户id
     *
     * @param commentUserId 发布留言的用户id
     */
    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    /**
     * 获取冗余用户昵称，非一致性字段，用户修改昵称后可以不用同步
     *
     * @return comment_user_nickname - 冗余用户昵称，非一致性字段，用户修改昵称后可以不用同步
     */
    public String getCommentUserNickname() {
        return commentUserNickname;
    }

    /**
     * 设置冗余用户昵称，非一致性字段，用户修改昵称后可以不用同步
     *
     * @param commentUserNickname 冗余用户昵称，非一致性字段，用户修改昵称后可以不用同步
     */
    public void setCommentUserNickname(String commentUserNickname) {
        this.commentUserNickname = commentUserNickname;
    }

    /**
     * 获取冗余的用户头像
     *
     * @return comment_user_face - 冗余的用户头像
     */
    public String getCommentUserFace() {
        return commentUserFace;
    }

    /**
     * 设置冗余的用户头像
     *
     * @param commentUserFace 冗余的用户头像
     */
    public void setCommentUserFace(String commentUserFace) {
        this.commentUserFace = commentUserFace;
    }

    /**
     * 获取留言内容
     *
     * @return content - 留言内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置留言内容
     *
     * @param content 留言内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取留言时间
     *
     * @return create_time - 留言时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置留言时间
     *
     * @param createTime 留言时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}