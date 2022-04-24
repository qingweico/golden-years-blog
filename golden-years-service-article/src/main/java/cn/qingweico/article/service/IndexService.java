package cn.qingweico.article.service;

import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;

import java.util.List;
import java.util.Map;

/**
 * @author zqw
 * @date 2022/4/18
 */
public interface IndexService {
    /**
     * 根据文章状态获取所有的文章数量
     * {@link ArticleReviewStatus}
     * {@link YesOrNo} 1: 非物理删除; 0: 未删除
     *
     * @return {@code Integer}文章数量
     */
    Integer getArticleCounts();


    /**
     * 获取全站评论数量
     *
     * @return {@code Integer}评论数量
     */
    Integer getCommentCount();

    /**
     * 获取每个标签下文章的数量
     *
     * @return List<TagVO>
     */
    List<Map<String, Object>> getBlogCountByTag();

    /**
     * 获取一年内的文章贡献数
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getBlogContributeCount();
}
