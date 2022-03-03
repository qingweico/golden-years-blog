package cn.qingweico.article.service;

import cn.qingweico.util.PagedGridResult;

/**
 * 主页评论服务
 *
 * @author zqw
 * @date 2021/9/13
 */
public interface CommentPortalService {

    /**
     * 发表评论
     *
     * @param articleId       文章id
     * @param fatherCommentId 回复评论id
     * @param content         发表的内容
     * @param userId          发表评论的用户id
     * @param nickname        发表评论的用户昵称
     * @param face            发表评论的用户头像
     */
    void publishComment(String articleId,
                        String fatherCommentId,
                        String content,
                        String userId,
                        String nickname,
                        String face);


    /**
     * 查询文章的评论列表
     *
     * @param articleId 文章id
     * @param page      分页起始
     * @param pageSize  每页显示的数目
     * @return PagedGridResult
     */
    PagedGridResult queryArticleComments(String articleId,
                                         Integer page,
                                         Integer pageSize);

    /**
     * 查询作家文章下所有的评论
     *
     * @param writerId 作家id
     * @param page     分页起始
     * @param pageSize 每页显示的数目
     * @return PagedGridResult
     */
    PagedGridResult queryWriterComments(String writerId,
                                        Integer page,
                                        Integer pageSize);

    /**
     * 作家删除评论
     *
     * @param writerId  作家id
     * @param commentId 评论id
     */

    void delete(String writerId, String commentId);


}
