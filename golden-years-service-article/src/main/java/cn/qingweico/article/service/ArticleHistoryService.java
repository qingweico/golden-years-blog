package cn.qingweico.article.service;

import cn.qingweico.entity.Article;
import cn.qingweico.entity.History;
import cn.qingweico.enums.ArticleHistoryDeleteType;
import cn.qingweico.util.PagedResult;

import java.util.List;

/**
 * @author zqw
 * @date 2022/5/6
 */
public interface ArticleHistoryService {

    /**
     * 获取用户的文章浏览历史
     *
     * @param userId   用户id
     * @param pageNum  起始分页
     * @param pageSize 每页的数量
     * @return PagedResult<History>
     */
    PagedResult getHistoryList(String userId, Integer pageNum, Integer pageSize);


    /**
     * 用户浏览文章, 增加一条浏览记录
     *
     * @param userId    用户id
     * @param articleId 文章id
     */
    void addHistory(String userId, String articleId);


    /**
     * 用户删除文章浏览历史
     *
     * @param userId      用户id
     * @param deleteModel {@code Integer} 为null 则删除全部 否则 {@link ArticleHistoryDeleteType}
     */
    void deleteHistory(String userId, Integer deleteModel);


    /**
     * 根据文章id查询文章信息
     *
     * @param id 文章id
     * @return Article
     */
    Article queryArticleById(String id);
}
