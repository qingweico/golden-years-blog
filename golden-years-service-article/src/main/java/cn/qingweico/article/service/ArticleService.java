package cn.qingweico.article.service;

import cn.qingweico.pojo.bo.NewArticleBO;
import cn.qingweico.util.PagedGridResult;

import java.util.Date;

/**
 * 用户和管理员相关的文章服务
 *
 * @author:qiming
 * @date: 2021/9/11
 */
public interface ArticleService {

    /**
     * 用户发布新的新文章
     *
     * @param newArticleBO NewArticleBO
     */
    void createArticle(NewArticleBO newArticleBO);


    /**
     * 定时发布文章
     */
    void timedPublishArticle();

    /**
     * 修改文章的状态为及时发布
     *
     * @param articleId 文章id
     */
    void timelyPublishArticle(String articleId);

    /**
     * 查询我的文章列表
     *
     * @return PagedGridResult
     */
    PagedGridResult queryMyArticles(String userId,
                                    String keyword,
                                    Integer status,
                                    Date startDate,
                                    Date endDate,
                                    Integer page,
                                    Integer pageSize);


    /**
     * 修改文章的状态
     *
     * @param articleId     文章id
     * @param pendingStatus 待修改为的状态
     */
    void updateArticleStatus(String articleId, Integer pendingStatus);


    /**
     * 删除文章
     */
    void deleteArticle(String userId, String articleId);

    /**
     * 撤回文章
     */
    void withdrawArticle(String userId, String articleId);

    /**
     * 关联文章和GridFS的html文件的id
     *
     * @param articleId      文章id
     * @param articleMongoId 上传至mongodb中文章的mongoid
     */
    void updateArticleToGridFS(String articleId, String articleMongoId);

    /**
     * 管理员查询所有的文章列表
     *
     * @param status       文章的状态
     * @param page         page 起始分页
     * @param pageSize     pageSize 每页的数目
     * @param deleteStatus 文章的逻辑状态(0:未删除 1: 已删除)
     * @return PagedGridResult
     */
    PagedGridResult queryAll(Integer status, Integer page, Integer pageSize, Integer deleteStatus);

    /**
     * 管理员删除文章(delete sql)
     */
    void deleteArticle(String articleId);

    /**
     * 重新对文章进行审核
     */
    void reReview(String articleId);

    /**
     * 管理员撤回用户对文章的删除
     */
    void withdrawDelete(String articleId);


}
