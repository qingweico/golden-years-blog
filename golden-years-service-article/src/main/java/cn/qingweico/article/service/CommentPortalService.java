package cn.qingweico.article.service;

import cn.qingweico.pojo.bo.CommentReplyBO;
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
     * @param commentReplyBO {@link CommentReplyBO}
     */
    void publishComment(CommentReplyBO commentReplyBO);


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
     * 查询作者文章下所有的评论
     *
     * @param userId 作家id
     * @param page   分页起始
     * @param pageSize 每页显示的数目
     * @return PagedGridResult
     */
    PagedGridResult queryUserComments(String userId,
                                      Integer page,
                                      Integer pageSize);

    /**
     * 作者删除评论
     *
     * @param commentId 评论id
     */

    void delete(String commentId);


}
