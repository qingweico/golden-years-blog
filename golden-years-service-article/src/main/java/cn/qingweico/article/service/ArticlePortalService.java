package cn.qingweico.article.service;

import cn.qingweico.entity.Article;
import cn.qingweico.entity.Category;
import cn.qingweico.entity.Tag;
import cn.qingweico.entity.model.ArticleDetail;
import cn.qingweico.util.PagedResult;

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
     * @param tag      按照文章标签查询
     * @param page     起始查询页面
     * @param pageSize 每页查询的数量
     * @return {@link PagedResult}
     */
    PagedResult queryPortalArticleList(String keyword,
                                       String category,
                                       String tag,
                                       Integer page,
                                       Integer pageSize);

    /**
     * 查询主页热门文章
     *
     * @return PagedGridResult
     */
    List<Article> queryHotArticle();

    /**
     * 查询作家发布的所有文章列表
     *
     * @param author   作家id
     * @param page     起始查询页面
     * @param pageSize 每页查询的数量
     * @return {@link PagedResult}
     */
    PagedResult queryArticleListOfAuthor(String author,
                                         Integer page,
                                         Integer pageSize);

    /**
     * 查询作者主页文章排行
     *
     * @param author 作者id
     * @return List<Article>
     */
    List<Article> queryGoodArticleListOfAuthor(String author);

    /**
     * 文章详情
     *
     * @param articleId 文章id
     * @return ArticleDetailVO
     */
    ArticleDetail queryDetail(String articleId);

    /**
     * 查询首页每个类别下文章的数目
     *
     * @param categoryId 类别id
     * @return Integer 每个类别下文章的数目
     */
    Integer queryEachCategoryArticleCount(String categoryId);

    /**
     * 查询首页文章类别
     *
     * @return List<Category>
     */
    List<Category> queryCategoryList();

    /**
     * 根据时间范围归类文章
     *
     * @param yearAndMonth 年份和月份
     * @param page         起始查询页面
     * @param pageSize     每页查询的数量
     * @return PagedGridResult
     */
    PagedResult getArticleListByTime(String yearAndMonth,
                                     Integer page,
                                     Integer pageSize);


    /**
     * 获取归档时间列表
     *
     * @param userId 用户id
     * @return List<String>
     */
    List<String> queryArchiveTimeList(String userId);

    /**
     * 主页时间线功能
     *
     * @param userId   用户id
     * @param page     起始查询页面
     * @param pageSize 每页查询的数量
     * @return PagedGridResult
     */
    PagedResult timeline(String userId, Integer page, Integer pageSize);

    /**
     * 主页分类功能
     *
     * @param userId     用户id
     * @param categoryId 类别id
     * @param page       起始分页
     * @param pageSize   每页数量
     * @return PagedGridResult PagedGridResult
     */
    PagedResult queryArticleListByCategoryId(String userId,
                                             String categoryId,
                                             Integer page,
                                             Integer pageSize);

    /**
     * 获取文章的标签
     *
     * @param article Article
     * @return List<Tag>
     */
    List<Tag> getTagList(Article article);
}
