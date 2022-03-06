package cn.qingweico.article.service;

import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.util.PagedGridResult;

import java.util.List;


/**
 * 主页文章服务
 *
 * @author zqw
 * @date 2021/9/12
 */
public interface ArticlePortalService {

    /**
     * 查询主页文章列表
     *
     * @param keyword  查询关键字
     * @param category 按照文章类别查询
     * @param page     起始查询页面
     * @param pageSize 每页查询的数量
     * @return {@link PagedGridResult}
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
     *
     * @param author   作家id
     * @param page     起始查询页面
     * @param pageSize 每页查询的数量
     * @return {@link PagedGridResult}
     */
    PagedGridResult queryArticleListOfAuthor(String author,
                                             Integer page,
                                             Integer pageSize);

    /**
     * 查询作家主页近期佳文
     *
     * @param author 作家id
     * @return {@link PagedGridResult}
     */
    PagedGridResult queryGoodArticleListOfAuthor(String author);

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

    /**
     * 根据时间范围归类文章
     * @param yearAndMonth 年份和月份
     * @return List<Category>
     */
    List<Article> getArticleListByTime(String yearAndMonth);
}
