package cn.qingweico.article.service;

import cn.qingweico.pojo.bo.NewArticleBO;
import cn.qingweico.pojo.vo.ArticleAdminVO;
import cn.qingweico.util.PagedGridResult;

import java.util.Date;
import java.util.List;

/**
 * 用户和管理员相关的文章服务
 *
 * @author zqw
 * @date 2021/9/11
 */
public interface ArticleService {

    /**
     * 用户发布新的新文章
     *
     * @param newArticleBO {@link  NewArticleBO}
     */
    void createArticle(NewArticleBO newArticleBO);


    /**
     * 定时发布文章
     */
    void timedPublishArticle();

    /**
     * 修改文章的状态为立即发布
     *
     * @param articleId 文章id
     */
    void timelyPublishArticle(String articleId);


    /**
     * 查询我的文章列表
     *
     * @param userId     userId
     * @param keyword    keyword
     * @param categoryId categoryId
     * @param status     status
     * @param startDate  startDate
     * @param endDate    endDate
     * @param page       page
     * @param pageSize   pageSize
     * @return PagedGridResult
     */
    PagedGridResult queryUserArticles(String userId,
                                      String keyword,
                                      String categoryId,
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
     * 修改文章
     *
     * @param newArticleBO {@link  NewArticleBO}
     */
    void updateArticle(NewArticleBO newArticleBO);


    /**
     * 删除文章
     *
     * @param userId    用户id
     * @param articleId 文章id
     */
    void deleteArticle(String userId, String articleId);

    /**
     * 撤回文章
     *
     * @param userId    用户id
     * @param articleId 文章id
     */
    void withdrawArticle(String userId, String articleId);

    /**
     * 关联文章和GridFS的html文件的id
     *
     * @param articleId      文章id
     * @param articleMongoId 上传至mongodb中文章的mongoid
     */
    void updateArticleToGridFs(String articleId, String articleMongoId);

    /**
     * 管理员查询所有的文章列表
     *
     * @param keyword      文章标题
     * @param status       文章的状态
     * @param categoryId   文章分类
     * @param tagId        文章标签
     * @param deleteStatus 文章的逻辑状态(0:未删除 1: 已删除)
     * @param startDateStr 文章发布时间(开始)
     * @param endDateStr   文章发布时间(截止)
     * @param page         page 起始分页
     * @param pageSize     pageSize 每页的数目
     * @return PagedGridResult
     */
    PagedGridResult query(String keyword,
                               Integer status,
                               String categoryId,
                               String tagId,
                               Integer deleteStatus,
                               Date startDateStr,
                               Date endDateStr,
                               Integer page,
                               Integer pageSize);

    /**
     * 管理员删除文章(delete sql)
     *
     * @param articleId 文章id
     */
    void deleteArticle(String articleId);

    /**
     * 重新对文章进行审核
     *
     * @param articleId 文章id
     */
    void reReview(String articleId);

    /**
     * 管理员撤回用户对文章的删除
     *
     * @param articleId 文章id
     */
    void withdrawDelete(String articleId);
}
