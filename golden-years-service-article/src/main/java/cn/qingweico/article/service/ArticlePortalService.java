package cn.qingweico.article.service;

import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.util.PagedGridResult;

import java.util.List;


/**
 * 主页文章服务
 *
 * @author:qiming
 * @date: 2021/9/12
 */
public interface ArticlePortalService {

    /**
     * 查询主页文章列表
     *
     * @return PagedGridResult
     */
    PagedGridResult queryPortalArticleList(String keyword,
                                           Integer category,
                                           Integer page,
                                           Integer pageSize);

    /**
     * 查询主页热门文章
     *
     * @return List<Article>
     */
    List<Article> queryHotNews();

    /**
     * 查询作家发布的所有文章列表
     */
    PagedGridResult queryArticleListOfWriter(String writerId,
                                             Integer page,
                                             Integer pageSize);

    /**
     * 查询作家页面近期佳文
     */
    PagedGridResult queryGoodArticleListOfWriter(String writerId);

    /**
     * 文章详情
     *
     * @param articleId 文章id
     * @return ArticleDetailVO
     */
    ArticleDetailVO queryDetail(String articleId);

    /**
     * 查询首页每个类别下文章的数目
     *
     * @param categoryId 类别id
     * @return Integer
     */
    Integer queryEachCategoryCount(Integer categoryId);

    /**
     * 查询首页文章类别
     *
     * @return List<Category>
     */
    List<Category> queryCategoryList();
}
