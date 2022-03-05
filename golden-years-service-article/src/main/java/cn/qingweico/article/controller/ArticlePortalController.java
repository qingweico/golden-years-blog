package cn.qingweico.article.controller;

import cn.qingweico.api.controller.BaseController;
import cn.qingweico.api.controller.article.ArticlePortalControllerApi;
import cn.qingweico.article.clients.UserBaseInfoClient;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.global.Constants;
import cn.qingweico.global.RedisConf;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.eo.ArticleEo;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.pojo.vo.CategoryVO;
import cn.qingweico.pojo.vo.IndexArticleVO;
import cn.qingweico.pojo.vo.UserBasicInfoVO;
import cn.qingweico.util.IpUtils;
import cn.qingweico.util.JsonUtils;
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
 * @author zqw
 * @date 2021/9/12
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
        AggregatedPage<ArticleEo> pagedArticle =
                new AggregatedPageImpl<>(new ArrayList<>(0));

        // 首页默认查询不带参数
        if (StringUtils.isBlank(keyword) && category == null) {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withPageable(pageable)
                    .build();

            pagedArticle = elasticsearchTemplate.queryForPage(searchQuery, ArticleEo.class);
        }

        // 按照文章类别查询
        if (StringUtils.isBlank(keyword) && category != null) {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.termQuery("categoryId", category))
                    .withPageable(pageable)
                    .build();

            pagedArticle = elasticsearchTemplate.queryForPage(searchQuery, ArticleEo.class);
        }

        String searchTitleField = "title";
        // 按照关键字查询, 并且高亮显示关键字
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
                    ArticleEo.class,
                    new SearchResultMapper() {
                        @Override
                        public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse,
                                                                Class<T> aClass,
                                                                Pageable pageable) {
                            List<ArticleEo> articleHighLightList = new ArrayList<>();
                            SearchHits searchHits = searchResponse.getHits();
                            for (SearchHit hit : searchHits) {
                                HighlightField highlightField = hit.getHighlightFields().get(searchTitleField);
                                String title = highlightField.getFragments()[0].toString();

                                String articleId = (String) hit.getSourceAsMap().get("id");
                                Integer categoryId = (Integer) hit.getSourceAsMap().get("categoryId");
                                Integer articleType = (Integer) hit.getSourceAsMap().get("articleType");
                                String articleCover = (String) hit.getSourceAsMap().get("articleCover");
                                String summary = (String) hit.getSourceAsMap().get("summary");
                                String author = (String) hit.getSourceAsMap().get("author");
                                Long dateLong = (Long) hit.getSourceAsMap().get("createTime");
                                Date createTime = new Date(dateLong);

                                ArticleEo articleEo = new ArticleEo();
                                articleEo.setId(articleId);
                                articleEo.setCategoryId(categoryId);
                                articleEo.setTitle(title);
                                articleEo.setArticleType(articleType);
                                articleEo.setArticleCover(articleCover);
                                articleEo.setSummary(summary);
                                articleEo.setAuthor(author);
                                articleEo.setCreateTime(createTime);

                                articleHighLightList.add(articleEo);
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

        List<ArticleEo> articleEoList = pagedArticle.getContent();
        List<Article> res = new ArrayList<>();

        for (ArticleEo articleEo : articleEoList) {
            Article article = new Article();
            BeanUtils.copyProperties(articleEo, article);
            res.add(article);
        }
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(res);
        gridResult.setCurrentPage(page + 1);
        gridResult.setTotalPage(pagedArticle.getTotalPages());
        gridResult.setRecords(pagedArticle.getTotalElements());
        return GraceJsonResult.ok(rebuildArticleGrid(gridResult));
    }

    @Override
    public GraceJsonResult query(String keyword,
                                 Integer category,
                                 Integer page,
                                 Integer pageSize) {
        if (page == null) {
            page = Constants.COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = Constants.COMMON_PAGE_SIZE;
        }
        PagedGridResult res = articlePortalService.queryPortalArticleList(keyword,
                category,
                page,
                pageSize);
        return GraceJsonResult.ok(rebuildArticleGrid(res));
    }

    @Override
    public GraceJsonResult queryEachCategoryArticleCount() {
        List<Category> categories = articlePortalService.queryCategoryList();
        // 查询每个类别下文章的数量
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
    public GraceJsonResult categoryList() {
        return GraceJsonResult.ok(articlePortalService.queryCategoryList());
    }

    @Override
    public GraceJsonResult hotList() {
        return GraceJsonResult.ok(articlePortalService.queryHotNews());
    }

    @Override
    public GraceJsonResult queryArticleListByAuthorId(String authorId,
                                                      Integer page,
                                                      Integer pageSize) {

        if (page == null) {
            page = Constants.COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = Constants.COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = articlePortalService.queryArticleListOfAuthor(authorId, page, pageSize);
        return GraceJsonResult.ok(rebuildArticleGrid(gridResult));
    }

    @Override
    public GraceJsonResult queryGoodArticleListOfAuthor(String writerId) {
        PagedGridResult gridResult = articlePortalService.queryGoodArticleListOfAuthor(writerId);
        return GraceJsonResult.ok(gridResult);
    }

    @Override
    public GraceJsonResult detail(String articleId) {
        ArticleDetailVO articleVO = articlePortalService.queryDetail(articleId);
        if (articleVO == null) {
            return new GraceJsonResult(ResponseStatusEnum.ARTICLE_NOT_EXIST);
        }
        Set<String> set = new HashSet<>();
        set.add(articleVO.getAuthorId());
        List<UserBasicInfoVO> authorList = getUserBasicInfoList(set);
        if (!authorList.isEmpty()) {
            articleVO.setAuthorName(authorList.get(0).getNickname());
            articleVO.setAuthorFace(authorList.get(0).getFace());
        }
        articleVO.setReadCounts(getCountsFromRedis(RedisConf.REDIS_ARTICLE_READ_COUNTS + Constants.SYMBOL_COLON + articleId));
        if (authorList.size() == 0) {
            return new GraceJsonResult(ResponseStatusEnum.SYSTEM_ERROR, articleVO);
        } else {
            return GraceJsonResult.ok(articleVO);
        }
    }

    @Override
    public GraceJsonResult readArticle(String articleId, HttpServletRequest req) {
        String visitIp = IpUtils.getRequestIp(req);
        redisOperator.setnx60s(RedisConf.REDIS_ARTICLE_ALREADY_READ + Constants.SYMBOL_COLON + articleId + Constants.SYMBOL_COLON + visitIp, visitIp);
        redisOperator.increment(RedisConf.REDIS_ARTICLE_READ_COUNTS + Constants.SYMBOL_COLON + articleId, 1);
        return GraceJsonResult.ok();
    }


    @Override
    public Integer readCounts(String articleId, HttpServletRequest req) {
        return getCountsFromRedis(RedisConf.REDIS_ARTICLE_READ_COUNTS + Constants.SYMBOL_COLON + articleId);
    }

    private UserBasicInfoVO getAuthorInfoIfPresent(String author,
                                                   List<UserBasicInfoVO> authorList) {
        if (authorList == null) {
            return null;
        }
        for (UserBasicInfoVO userVO : authorList) {
            if (userVO.getId().equals(author)) {
                return userVO;
            }
        }
        return null;
    }

    private PagedGridResult rebuildArticleGrid(PagedGridResult gridResult) {
        String articleListJson = JsonUtils.objectToJson(gridResult.getRows());
        List<Article> rows = JsonUtils.jsonToList(articleListJson, Article.class);
        Set<String> idSet = new HashSet<>();
        List<String> idList = new ArrayList<>();
        for (Article article : rows) {
            idSet.add(article.getAuthorId());
            idList.add(RedisConf.REDIS_ARTICLE_READ_COUNTS + Constants.SYMBOL_COLON + article.getId());
        }
        // redis mget
        List<String> readCountsRedisList = redisOperator.mget(idList);

        List<UserBasicInfoVO> authorList = getUserBasicInfoList(idSet);
        List<IndexArticleVO> indexArticleList = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            Article article = rows.get(i);
            IndexArticleVO indexArticleVO = new IndexArticleVO();
            BeanUtils.copyProperties(article, indexArticleVO);
            UserBasicInfoVO authorInfo = getAuthorInfoIfPresent(article.getAuthorId(), authorList);
            indexArticleVO.setAuthorVO(authorInfo);
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

    @Resource
    private UserBaseInfoClient client;

    public List<UserBasicInfoVO> getUserBasicInfoList(Set<?> idSet) {
        List<UserBasicInfoVO> userBasicInfoVOList;
        GraceJsonResult result = client.getUserBasicInfoList(JsonUtils.objectToJson(idSet));
        if (result != null) {
            String userJson = JsonUtils.objectToJson(result.getData());
            userBasicInfoVOList = JsonUtils.jsonToList(userJson, UserBasicInfoVO.class);
        } else {
            return new ArrayList<>(0);
        }
        return userBasicInfoVOList;
    }
}
