package cn.qingweico.article.controller;

import cn.qingweico.api.controller.BaseController;
import cn.qingweico.api.controller.article.ArticlePortalControllerApi;
import cn.qingweico.article.clients.UserBaseInfoClient;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.grace.result.GraceJsonResult;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.eo.ArticleEO;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.pojo.vo.CategoryVO;
import cn.qingweico.pojo.vo.IndexArticleVO;
import cn.qingweico.pojo.vo.UserBasicInfoVO;
import cn.qingweico.util.IpUtils;
import cn.qingweico.util.JSONUtils;
import cn.qingweico.util.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author:qiming
 * @date: 2021/9/12
 */
@RestController
public class ArticlePortalController extends BaseController implements ArticlePortalControllerApi {
    final static Logger log = LoggerFactory.getLogger(ArticlePortalController.class);

    @Resource
    private ArticlePortalService articlePortalService;


    @Override
    public GraceJsonResult queryAllViaEs(String keyword,
                                         Integer category,
                                         Integer page,
                                         Integer pageSize) {
        page--;
        Pageable pageable = PageRequest.of(page, pageSize);

        SearchQuery searchQuery;
        AggregatedPage<ArticleEO> pagedArticle =
                new AggregatedPageImpl<>(new ArrayList<>(0));

        // 首页默认查询不带参数
        if (StringUtils.isBlank(keyword) && category == null) {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withPageable(pageable)
                    .build();

            pagedArticle = elasticsearchTemplate.queryForPage(searchQuery, ArticleEO.class);
        }

        // 按照文章类别查询
        if (StringUtils.isBlank(keyword) && category != null) {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.termQuery("categoryId", category))
                    .withPageable(pageable)
                    .build();

            pagedArticle = elasticsearchTemplate.queryForPage(searchQuery, ArticleEO.class);
        }

        String searchTitleField = "title";
        // 按照关键字查询 并且高亮显示关键字
        if (StringUtils.isNotBlank(keyword) && category == null) {
            String preTag = "<font color='red'>";
            String postTag = "</font>";
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchQuery(searchTitleField, keyword))
                    .withHighlightFields(new HighlightBuilder.Field(searchTitleField)
                            .preTags(preTag)
                            .postTags(postTag))
                    .withPageable(pageable)
                    .build();

            pagedArticle = elasticsearchTemplate.queryForPage(searchQuery,
                    ArticleEO.class,
                    new SearchResultMapper() {
                        @Override
                        public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse,
                                                                Class<T> aClass,
                                                                Pageable pageable) {
                            List<ArticleEO> articleHighLightList = new ArrayList<>();
                            SearchHits searchHits = searchResponse.getHits();
                            for (SearchHit hit : searchHits) {
                                HighlightField highlightField = hit.getHighlightFields().get(searchTitleField);
                                String title = highlightField.getFragments()[0].toString();

                                String articleId = (String) hit.getSourceAsMap().get("id");
                                Integer categoryId = (Integer) hit.getSourceAsMap().get("categoryId");
                                Integer articleType = (Integer) hit.getSourceAsMap().get("articleType");
                                String articleCover = (String) hit.getSourceAsMap().get("articleCover");
                                String brief = (String) hit.getSourceAsMap().get("brief");
                                String publishUserId = (String) hit.getSourceAsMap().get("publishUserId");
                                Long dateLong = (Long) hit.getSourceAsMap().get("publishTime");
                                Date publishTime = new Date(dateLong);

                                ArticleEO articleEO = new ArticleEO();
                                articleEO.setId(articleId);
                                articleEO.setCategoryId(categoryId);
                                articleEO.setTitle(title);
                                articleEO.setArticleType(articleType);
                                articleEO.setArticleCover(articleCover);
                                articleEO.setBrief(brief);
                                articleEO.setPublishUserId(publishUserId);
                                articleEO.setPublishTime(publishTime);

                                articleHighLightList.add(articleEO);
                            }
                            return new AggregatedPageImpl<>((List<T>) articleHighLightList,
                                    pageable, searchResponse.getHits().totalHits);
                        }

                        @Override
                        public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                            return null;
                        }
                    });
        }

        List<ArticleEO> articleEOList = pagedArticle.getContent();
        List<Article> res = new ArrayList<>();

        for (ArticleEO articleEO : articleEOList) {
            Article article = new Article();
            BeanUtils.copyProperties(articleEO, article);
            article.setContent(articleEO.getBrief());
            res.add(article);
        }
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(res);
        gridResult.setPage(page + 1);
        gridResult.setTotal(pagedArticle.getTotalPages());
        gridResult.setRecords(pagedArticle.getTotalElements());
        return GraceJsonResult.ok(rebuildArticleGrid(gridResult));
    }

    @Override
    public GraceJsonResult queryAll(String keyword,
                                    Integer category,
                                    Integer page,
                                    Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult res = articlePortalService.queryPortalArticleList(keyword,
                category,
                page,
                pageSize);
        return GraceJsonResult.ok(rebuildArticleGrid(res));
    }

    @Override
    public GraceJsonResult getCategoryList() {
        // 缓存
        String categoriesJSON = redisOperator.get(REDIS_ALL_CATEGORY);

        List<Category> categories;
        if (StringUtils.isBlank(categoriesJSON)) {
            categories = articlePortalService.queryCategoryList();
            redisOperator.set(REDIS_ALL_CATEGORY, JSONUtils.objectToJson(categories));
            log.info("类别信息已存入缓存");
        } else {
            categories = JSONUtils.jsonToList(categoriesJSON, Category.class);
        }
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        for (Category category : categories) {
            CategoryVO categoryVO = new CategoryVO();
            BeanUtils.copyProperties(category, categoryVO);
            int categoryCount = articlePortalService.queryEachCategoryCount(category.getId());
            categoryVO.setEachCategoryArticleCount(categoryCount);
            categoryVOList.add(categoryVO);
        }
        return GraceJsonResult.ok(categoryVOList);
    }

    @Override
    public GraceJsonResult hotList() {
        return GraceJsonResult.ok(articlePortalService.queryHotNews());
    }

    @Override
    public GraceJsonResult queryArticleListOfWriter(String writerId,
                                                    Integer page,
                                                    Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = articlePortalService.queryArticleListOfWriter(writerId, page, pageSize);
        return GraceJsonResult.ok(rebuildArticleGrid(gridResult));
    }

    @Override
    public GraceJsonResult queryGoodArticleListOfWriter(String writerId) {
        PagedGridResult gridResult = articlePortalService.queryGoodArticleListOfWriter(writerId);
        return GraceJsonResult.ok(gridResult);
    }

    @Override
    public GraceJsonResult detail(String articleId) {
        ArticleDetailVO articleVO = articlePortalService.queryDetail(articleId);
        Set<String> set = new HashSet<>();
        set.add(articleVO.getPublishUserId());
        List<UserBasicInfoVO> publisherList = getUserBasicInfoList(set);
        if (!publisherList.isEmpty()) {
            articleVO.setPublishUserName(publisherList.get(0).getNickname());
            articleVO.setPublishUserFace(publisherList.get(0).getFace());
        }
        articleVO.setReadCounts(getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId));
        return GraceJsonResult.ok(articleVO);
    }

    @Override
    public GraceJsonResult readArticle(String articleId, HttpServletRequest req) {
        String visitIP = IpUtils.getRequestIp(req);
        redisOperator.setnx60s(REDIS_ARTICLE_ALREADY_READ + ":" + articleId + ":" + visitIP, visitIP);
        redisOperator.increment(REDIS_ARTICLE_READ_COUNTS + ":" + articleId, 1);
        return GraceJsonResult.ok();
    }


    @Override
    public Integer readCounts(String articleId, HttpServletRequest req) {
        return getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId);
    }

    private UserBasicInfoVO getUserIfPublisher(String publisher,
                                               List<UserBasicInfoVO> publisherList) {
        if (publisherList == null) {
            return null;
        }
        for (UserBasicInfoVO userVO : publisherList) {
            if (userVO.getId().equals(publisher)) {
                return userVO;
            }
        }
        return null;
    }

    private PagedGridResult rebuildArticleGrid(PagedGridResult gridResult) {

        List<Article> rows = (List<Article>) gridResult.getRows();

        Set<String> idSet = new HashSet<>();
        List<String> idList = new ArrayList<>();
        for (Article article : rows) {
            idSet.add(article.getPublishUserId());
            idList.add(REDIS_ARTICLE_READ_COUNTS + ":" + article.getId());
        }
        // redis mget
        List<String> readCountsRedisList = redisOperator.mget(idList);

        List<UserBasicInfoVO> publisherList = getUserBasicInfoList(idSet);
        List<IndexArticleVO> indexArticleList = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            Article article = rows.get(i);
            IndexArticleVO indexArticleVO = new IndexArticleVO();
            BeanUtils.copyProperties(article, indexArticleVO);
            UserBasicInfoVO publisher = getUserIfPublisher(article.getPublishUserId(), publisherList);
            indexArticleVO.setPublisherVO(publisher);
            String redisCountsStr = readCountsRedisList.get(i);
            int readCounts = 0;
            if (StringUtils.isNotBlank(redisCountsStr)) {
                readCounts = Integer.parseInt(redisCountsStr);
            }
            indexArticleVO.setReadCounts(readCounts);
            indexArticleList.add(indexArticleVO);
        }
        gridResult.setRows(indexArticleList);
        return gridResult;
    }

    // 发起远程调用, 获得用户的基本信息
    @Resource
    private UserBaseInfoClient client;

    public List<UserBasicInfoVO> getUserBasicInfoList(Set<?> idSet) {
        List<UserBasicInfoVO> userBasicInfoVOList;
        GraceJsonResult result = client.getUserBasicInfoList(JSONUtils.objectToJson(idSet));
        if (result != null) {
            String userJson = JSONUtils.objectToJson(result.getData());
            userBasicInfoVOList = JSONUtils.jsonToList(userJson, UserBasicInfoVO.class);
        } else {
            return new ArrayList<>(0);
        }
        return userBasicInfoVOList;
    }

}
