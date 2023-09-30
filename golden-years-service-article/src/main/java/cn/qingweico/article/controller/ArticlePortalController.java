package cn.qingweico.article.controller;

import cn.qingweico.core.base.BaseController;
import cn.qingweico.article.clients.UserBaseInfoClient;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.article.service.IndexService;
import cn.qingweico.entity.Article;
import cn.qingweico.entity.model.ArticleElastic;
import cn.qingweico.entity.model.IndexArticle;
import cn.qingweico.entity.model.UserArticleRank;
import cn.qingweico.entity.model.UserBasicInfo;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.result.Result;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zqw
 * @date 2021/9/12
 */
@Slf4j
@Api(value = "主页文章相关的接口定义", tags = {"主页文章相关的接口定义"})
@RequestMapping("/portal/article")
@RestController
public class ArticlePortalController extends BaseController {

    // 主站文章API

    @Resource
    private ArticlePortalService articlePortalService;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @ApiOperation(value = "首页查询文章列表(elasticSearch)", notes = "首页查询文章列表(elasticSearch)", httpMethod = "GET")
    @GetMapping("es/search")
    public Result articleListByElasticSearch(@RequestParam String keyword,
                                             @RequestParam String category,
                                             @RequestParam String tag,
                                             @RequestParam Integer page,
                                             @RequestParam Integer pageSize) {
        page--;
        Pageable pageable = PageRequest.of(page, pageSize);

        SearchQuery searchQuery;
        AggregatedPage<ArticleElastic> pagedArticle =
                new AggregatedPageImpl<>(new ArrayList<>(0));

        // 首页默认查询不带参数
        if (StringUtils.isBlank(keyword) && StringUtils.isBlank(category) && StringUtils.isBlank(tag)) {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withPageable(pageable)
                    .build();

            pagedArticle = elasticsearchTemplate.queryForPage(searchQuery, ArticleElastic.class);
        }

        // 按照文章类别查询
        if (StringUtils.isBlank(keyword) && !StringUtils.isBlank(category)) {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.termQuery(SysConst.CATEGORY_ID, category))
                    .withPageable(pageable)
                    .build();

            pagedArticle = elasticsearchTemplate.queryForPage(searchQuery, ArticleElastic.class);
        }
        // 按照文章标签查询
        if (StringUtils.isBlank(keyword) && !StringUtils.isBlank(tag)) {
            searchQuery = new NativeSearchQueryBuilder()
                    // tags: "" (string 空格隔开)
                    .withQuery(QueryBuilders.matchQuery(SysConst.TAGS, tag))
                    .withPageable(pageable)
                    .build();

            pagedArticle = elasticsearchTemplate.queryForPage(searchQuery, ArticleElastic.class);
        }

        // 按照关键字查询, 并且高亮显示关键字
        if (StringUtils.isNotBlank(keyword) && StringUtils.isBlank(category) && StringUtils.isBlank(tag)) {
            pagedArticle = queryByKeyword(keyword, pageable);
        }

        List<ArticleElastic> articleEoList = pagedArticle.getContent();
        List<Article> res = new ArrayList<>();
        for (ArticleElastic articleElastic : articleEoList) {
            Article article = new Article();
            BeanUtils.copyProperties(articleElastic, article);
            res.add(article);
        }
        PagedResult gridResult = new PagedResult();
        gridResult.setRows(res);
        gridResult.setCurrentPage(page + 1);
        gridResult.setTotalPage(pagedArticle.getTotalPages());
        gridResult.setRecords(pagedArticle.getTotalElements());
        return Result.ok(rebuildArticleGrid(gridResult));
    }

    private AggregatedPage<ArticleElastic> queryByKeyword(String keyword, Pageable pageable) {
        String preTag = "<font color='red'>";
        String postTag = "</font>";
        String searchTitleField = SysConst.TITLE;
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(searchTitleField, keyword))
                .withHighlightFields(new HighlightBuilder.Field(searchTitleField)
                        .preTags(preTag)
                        .postTags(postTag))
                .withPageable(pageable)
                .build();

        return elasticsearchTemplate.queryForPage(searchQuery,
                ArticleElastic.class,
                new SearchResultMapper() {
                    @Override
                    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse,
                                                            Class<T> aClass,
                                                            Pageable pageable) {
                        List<ArticleElastic> articleHighLightList = new ArrayList<>();
                        SearchHits searchHits = searchResponse.getHits();
                        for (SearchHit hit : searchHits) {
                            HighlightField highlightField = hit.getHighlightFields().get(searchTitleField);
                            String title = highlightField.getFragments()[0].toString();

                            String articleId = (String) hit.getSourceAsMap().get(SysConst.ID);
                            String categoryId = (String) hit.getSourceAsMap().get(SysConst.CATEGORY_ID);
                            Integer articleType = (Integer) hit.getSourceAsMap().get(SysConst.ARTICLE_TYPE);
                            String articleCover = (String) hit.getSourceAsMap().get(SysConst.ARTICLE_COVER);
                            String summary = (String) hit.getSourceAsMap().get(SysConst.SUMMARY);
                            String authorId = (String) hit.getSourceAsMap().get(SysConst.AUTHOR_ID);
                            Long dateLong = (Long) hit.getSourceAsMap().get(SysConst.CREATE_TIME);
                            Date createTime = new Date(dateLong);

                            ArticleElastic articleElastic = new ArticleElastic();
                            articleElastic.setId(articleId);
                            articleElastic.setCategoryId(categoryId);
                            articleElastic.setTitle(title);
                            articleElastic.setArticleType(articleType);
                            articleElastic.setArticleCover(articleCover);
                            articleElastic.setSummary(summary);
                            articleElastic.setAuthorId(authorId);
                            articleElastic.setCreateTime(createTime);
                            articleHighLightList.add(articleElastic);
                        }
                        @SuppressWarnings("unchecked")
                        AggregatedPageImpl<T> ts = new AggregatedPageImpl<>((List<T>) articleHighLightList,
                                pageable, searchResponse.getHits().totalHits);
                        return ts;
                    }

                    @Override
                    public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                        return null;
                    }
                });
    }

    @ApiOperation(value = "首页查询文章列表(sql)", notes = "首页查询文章列表(sql)", httpMethod = "GET")
    @GetMapping("search")
    public Result articleListBySql(@RequestParam String keyword,
                                   @RequestParam String category,
                                   @RequestParam String tag,
                                   @RequestParam Integer page,
                                   @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedResult res = articlePortalService.queryPortalArticleList(keyword,
                category,
                tag,
                page,
                pageSize);
        return Result.ok(rebuildArticleGrid(res));
    }

    @ApiOperation(value = "首页查询分类列表", notes = "首页查询分类列表", httpMethod = "GET")
    @GetMapping("getCategoryList")
    public Result getCategoryList() {
        return Result.ok(articlePortalService.queryCategoryList());
    }

    @ApiOperation(value = "首页文章排行", notes = "首页文章排行", httpMethod = "GET")
    @GetMapping("/rank")
    public Result hotList(@RequestParam Integer page,
                          @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        long size = redisCache.zSetSize(RedisConst.Z_SET_ARTICLE_RANK);
        // 如果zset key不存在则返回0
        if (size == 0) {
            List<Article> list = articlePortalService.queryHotArticle();
            for (Article article : list) {
                int influence = computeArticleInfluence(article.getId());
                String articleJson = JsonUtils.objectToJson(article);
                size = list.size();
                redisCache.zSetAdd(RedisConst.Z_SET_ARTICLE_RANK, articleJson, influence);
                // 并设置5分钟过期时间即文章排行榜的刷新时间为5分钟
                redisCache.expire(RedisConst.Z_SET_ARTICLE_RANK, 5, TimeUnit.MINUTES);
            }
        }
        Set<String> articleJson = redisCache.zSetRevRange(RedisConst.Z_SET_ARTICLE_RANK, (long) (page - 1) * pageSize,
                (pageSize - 1) + (long) (page - 1) * pageSize);

        List<Article> result = new ArrayList<>();
        for (String json : articleJson) {
            Article article = JsonUtils.jsonToPojo(json, Article.class);
            result.add(article);
        }
        PagedResult pgr = new PagedResult();
        pgr.setCurrentPage(page);
        pgr.setRecords(size);
        pgr.setTotalPage((size / pageSize) + 1);
        pgr.setRows(result);
        return Result.ok(rebuildArticleGrid(pgr));
    }

    @GetMapping("/homepage")
    @ApiOperation(value = "查询作者发布的所有文章列表", notes = "查询作者发布的所有文章列表", httpMethod = "GET")
    public Result queryArticleListByAuthorId(@RequestParam String authorId,
                                             @RequestParam Integer page,
                                             @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedResult gridResult = articlePortalService.queryArticleListOfAuthor(authorId, page, pageSize);
        return Result.ok(rebuildArticleGrid(gridResult));
    }

    @GetMapping("rank/{id}")
    @ApiOperation(value = "查询个人中心热门文章", notes = "查询个人中心热门文章", httpMethod = "GET")
    public Result queryGoodArticleListOfAuthor(@PathVariable("id") String authorId) {
        long size = redisCache.zSetSize(RedisConst.Z_SET_ARTICLE_USER_RANK);
        if (size == 0) {
            List<Article> list = articlePortalService.queryGoodArticleListOfAuthor(authorId);
            for (Article article : list) {
                int influence = computeArticleInfluence(article.getId());
                String articleJson = JsonUtils.objectToJson(article);
                redisCache.zSetAdd(RedisConst.Z_SET_ARTICLE_USER_RANK, articleJson, influence);
                redisCache.expire(RedisConst.Z_SET_ARTICLE_USER_RANK, 5, TimeUnit.MINUTES);
            }
        }
        // 默认只展示5篇热门文章
        Set<String> articleJson = redisCache.zSetRevRange(RedisConst.Z_SET_ARTICLE_USER_RANK, SysConst.NUM_ZERO, SysConst.NUM_FOUR);
        List<UserArticleRank> result = new ArrayList<>();
        for (String json : articleJson) {
            Article article = JsonUtils.jsonToPojo(json, Article.class);
            UserArticleRank userArticleRank = new UserArticleRank();
            BeanUtils.copyProperties(article == null ? new Article() : article, userArticleRank);
            result.add(userArticleRank);
        }
        return Result.ok(result);
    }

    @GetMapping("getArticleByTime")
    @ApiOperation(value = "通过时间归类文章", notes = "通过时间归类文章", httpMethod = "GET")
    public Result queryArticleByTime(@RequestParam Integer page,
                                     @RequestParam Integer pageSize,
                                     @RequestParam String monthAndYear) {
        checkPagingParams(page, pageSize);
        PagedResult articleArchiveVOList = articlePortalService.getArticleListByTime(monthAndYear, page, pageSize);
        return Result.ok(articleArchiveVOList);
    }

    @GetMapping("getArchiveTimeList")
    @ApiOperation(value = "获取归档时间列表", notes = "获取归档时间列表", httpMethod = "GET")
    public Result getArchiveTimeList(@RequestParam String userId) {
        return Result.ok(articlePortalService.queryArchiveTimeList(userId));
    }

    @GetMapping("timeline")
    @ApiOperation(value = "主页文章时间线功能", notes = "主页文章时间线功能", httpMethod = "GET")
    public Result timeline(@RequestParam String userId,
                           @RequestParam Integer page,
                           @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedResult result = articlePortalService.timeline(userId, page, pageSize);
        return Result.ok(result);
    }

    @GetMapping("getArticleListByCategoryId")
    @ApiOperation(value = "主页分类功能", notes = "主页分类功能", httpMethod = "GET")
    public Result getArticleListByCategoryId(@RequestParam String userId,
                                             @RequestParam String categoryId,
                                             @RequestParam Integer page,
                                             @RequestParam Integer pageSize) {
        return Result.ok(articlePortalService.queryArticleListByCategoryId(userId,
                categoryId,
                page, pageSize));
    }

    // 文章全局状态

    @Resource
    private IndexService indexService;

    @ApiOperation(value = "获取全站文章数量", notes = "获取全站文章数量")
    @GetMapping("getArticleCounts")
    public Integer getArticleCounts() {
        return indexService.getArticleCounts();
    }

    @ApiOperation(value = "获取全站评论数量", notes = "获取全站评论数量")
    @GetMapping("getCommentCount")
    public Integer getCommentCount() {
        return indexService.getCommentCount();
    }

    @ApiOperation(value = "获取每个标签下文章数目", notes = "获取每个标签下文章数目")
    @GetMapping(value = "/getBlogCountByTag")
    public Result getBlogCountByTag() {
        return Result.ok(indexService.getBlogCountByTag());
    }

    @ApiOperation(value = "获取一年内的文章贡献数", notes = "获取一年内的文章贡献数")
    @GetMapping(value = "/getBlogContributeCount")
    public Result getBlogContributeCount() {
        return Result.ok(indexService.getBlogContributeCount());
    }

    // private methods

    private PagedResult rebuildArticleGrid(PagedResult gridResult) {
        String articleListJson = JsonUtils.objectToJson(gridResult.getRows());
        List<Article> rows = JsonUtils.jsonToList(articleListJson, Article.class);
        Set<String> idSet = new HashSet<>();
        List<String> idList = new ArrayList<>();
        for (Article article : rows) {
            idSet.add(article.getAuthorId());
            // 设置文章点赞数 收藏数等等
            idList.add(RedisConst.REDIS_ARTICLE_READ_COUNTS + SysConst.SYMBOL_COLON + article.getId());
            idList.add(RedisConst.REDIS_ARTICLE_STAR_COUNTS + SysConst.SYMBOL_COLON + article.getId());
            idList.add(RedisConst.REDIS_ARTICLE_COLLECT_COUNTS + SysConst.SYMBOL_COLON + article.getId());
            idList.add(RedisConst.REDIS_ARTICLE_COMMENT_COUNTS + SysConst.SYMBOL_COLON + article.getId());


        }
        // redis multi get
        List<String> countsRedisList = redisCache.mGet(idList);

        List<UserBasicInfo> authorList = getUserInfoListByIdsClient(idSet);
        List<IndexArticle> indexArticleList = new ArrayList<>();
        int j = 0;
        for (Article article : rows) {
            IndexArticle indexArticle = new IndexArticle();
            BeanUtils.copyProperties(article, indexArticle);
            UserBasicInfo authorInfo = getAuthorInfoIfPresent(article.getAuthorId(), authorList);
            indexArticle.setAuthorVO(authorInfo);
            String readCountsStr;
            String starCountsStr;
            String collectCountsStr;
            String commentCountsStr;
            readCountsStr = countsRedisList.get(j++);
            starCountsStr = countsRedisList.get(j++);
            collectCountsStr = countsRedisList.get(j++);
            commentCountsStr = countsRedisList.get(j++);
            int readCounts = 0;
            int starCounts = 0;
            int collectCounts = 0;
            int commentCounts = 0;
            if (StringUtils.isNotBlank(readCountsStr)) {
                readCounts = Integer.parseInt(readCountsStr);
            }
            if (StringUtils.isNotBlank(starCountsStr)) {
                starCounts = Integer.parseInt(starCountsStr);
            }
            if (StringUtils.isNotBlank(collectCountsStr)) {
                collectCounts = Integer.parseInt(collectCountsStr);
            }
            if (StringUtils.isNotBlank(commentCountsStr)) {
                commentCounts = Integer.parseInt(commentCountsStr);
            }
            indexArticle.setReadCounts(readCounts);
            indexArticle.setStarCounts(starCounts);
            indexArticle.setCommentCounts(commentCounts);
            indexArticle.setCollectCounts(collectCounts);
            indexArticleList.add(indexArticle);
        }
        gridResult.setRows(indexArticleList);
        return gridResult;
    }

    private int computeArticleInfluence(String articleId) {
        String readCountsKey = RedisConst.REDIS_ARTICLE_READ_COUNTS + SysConst.SYMBOL_COLON + articleId;
        String collectCountsKey = RedisConst.REDIS_ARTICLE_READ_COUNTS + SysConst.SYMBOL_COLON + articleId;
        String starCountsKey = RedisConst.REDIS_ARTICLE_READ_COUNTS + SysConst.SYMBOL_COLON + articleId;
        String commentCountsKey = RedisConst.REDIS_ARTICLE_READ_COUNTS + SysConst.SYMBOL_COLON + articleId;
        int readCounts = getCountsFromRedis(readCountsKey);
        int collectCounts = getCountsFromRedis(collectCountsKey);
        int starCounts = getCountsFromRedis(starCountsKey);
        int commentCounts = getCountsFromRedis(commentCountsKey);
        double result = readCounts * 0.4 + starCounts * 0.3 + commentCounts * 0.2 + collectCounts * 0.1;
        return (int) result * 1000;
    }

    private UserBasicInfo getAuthorInfoIfPresent(String author,
                                                 List<UserBasicInfo> authorList) {
        if (authorList == null) {
            return null;
        }
        for (UserBasicInfo userVO : authorList) {
            if (userVO.getId().equals(author)) {
                return userVO;
            }
        }
        return null;
    }
    // 远程调用

    @Resource
    private UserBaseInfoClient client;

    public List<UserBasicInfo> getUserInfoListByIdsClient(Set<?> idSet) {
        List<UserBasicInfo> userBasicInfoVOList;
        Result result = client.queryByIds(JsonUtils.objectToJson(idSet));
        if (result != null) {
            String userJson = JsonUtils.objectToJson(result.getData());
            userBasicInfoVOList = JsonUtils.jsonToList(userJson, UserBasicInfo.class);
        } else {
            return new ArrayList<>(0);
        }
        return userBasicInfoVOList;
    }

}
