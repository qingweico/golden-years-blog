package cn.qingweico.article.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.mapper.CategoryMapper;
import cn.qingweico.article.mapper.TagMapper;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.global.RedisConf;
import cn.qingweico.global.SysConf;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.Tag;
import cn.qingweico.pojo.vo.*;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/12
 */
@Slf4j
@Service
public class ArticlePortalServiceImpl extends BaseService implements ArticlePortalService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private TagMapper tagMapper;

    @Override
    public PagedGridResult queryPortalArticleList(String keyword,
                                                  String category,
                                                  String tag,
                                                  Integer page,
                                                  Integer pageSize) {


        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExample(example);

        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike(SysConf.TITLE, SysConf.DELIMITER_PERCENT + keyword + SysConf.DELIMITER_PERCENT);
        }
        if (StringUtils.isNotBlank(category)) {
            criteria.andEqualTo(SysConf.CATEGORY_ID, category);
        }
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        if (StringUtils.isNotBlank(tag)) {
            // 标签筛选
            list = filterArticleTag(list, tag);;
        }
        return setterPagedGrid(list, page);
    }

    @Override
    public List<Article> queryHotArticle() {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SysConf.IS_DELETE, YesOrNo.NO.type);
        criteria.andEqualTo(SysConf.ARTICLE_STATUS, ArticleReviewStatus.SUCCESS.type);
        PageHelper.startPage(SysConf.NUM_ONE, SysConf.NUM_100);
        return articleMapper.selectByExample(example);
    }

    @Override
    public PagedGridResult queryArticleListOfAuthor(String author,
                                                    Integer page,
                                                    Integer pageSize) {

        return getPagedGridResult(author, page, pageSize);
    }

    private PagedGridResult getPagedGridResult(String author, Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExample(example);
        criteria.andEqualTo(SysConf.AUTHOR_ID, author);
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }

    @Override
    public List<Article> queryGoodArticleListOfAuthor(String author) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SysConf.IS_DELETE, YesOrNo.NO.type);
        criteria.andEqualTo(SysConf.ARTICLE_STATUS, ArticleReviewStatus.SUCCESS.type);
        criteria.andEqualTo(SysConf.AUTHOR_ID, author);
        return articleMapper.selectByExample(example);
    }

    @Override
    public ArticleDetailVO queryDetail(String articleId) {
        String cachedArticleDetail = redisOperator.get(RedisConf.REDIS_ARTICLE_DETAIL + SysConf.SYMBOL_COLON + articleId);
        ArticleDetailVO articleDetail = null;
        if (StringUtils.isNotBlank(cachedArticleDetail)) {
            articleDetail = JsonUtils.jsonToPojo(cachedArticleDetail, ArticleDetailVO.class);
        } else {
            Article article = new Article();
            article.setId(articleId);
            article.setIsAppoint(YesOrNo.NO.type);
            article.setIsDelete(YesOrNo.NO.type);
            article.setArticleStatus(ArticleReviewStatus.SUCCESS.type);
            Article result = articleMapper.selectOne(article);
            if (result != null) {
                List<Tag> tagList = getTagList(result);
                articleDetail = new ArticleDetailVO();
                articleDetail.setTagList(tagList);
                BeanUtils.copyProperties(result, articleDetail);
                redisOperator.set(RedisConf.REDIS_ARTICLE_DETAIL + SysConf.SYMBOL_COLON + articleId, JsonUtils.objectToJson(articleDetail));
                log.info("article detail has been cached, articleId: {}", articleId);
            }
        }
        return articleDetail;
    }

    @Override
    public List<Tag> getTagList(Article article) {
        // 获取文章标签
        String tags = article.getTags();
        List<Tag> tagList = new ArrayList<>();
        String[] tagIds = tags.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(",");

        for (String id : tagIds) {
            Tag tag = tagMapper.selectByPrimaryKey(id);
            tagList.add(tag);
        }
        return tagList;
    }

    @Override
    public Integer queryEachCategoryArticleCount(String categoryId) {
        Article article = new Article();
        article.setCategoryId(categoryId);
        articleMapper.select(article);
        return articleMapper.selectCount(article);
    }

    private Example.Criteria setDefaultArticleExample(Example example) {
        example.orderBy(SysConf.CREATE_TIME).desc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SysConf.IS_DELETE, YesOrNo.NO.type);
        criteria.andEqualTo(SysConf.IS_APPOINT, YesOrNo.NO.type);
        criteria.andEqualTo(SysConf.ARTICLE_STATUS, ArticleReviewStatus.SUCCESS.type);
        return criteria;
    }

    @Override
    public List<Category> queryCategoryList() {
        // 缓存
        String categoriesJson = redisOperator.get(RedisConf.REDIS_ARTICLE_CATEGORY);
        List<Category> categories;
        if (StringUtils.isBlank(categoriesJson)) {
            categories = categoryMapper.selectAll();
            redisOperator.set(RedisConf.REDIS_ARTICLE_CATEGORY, JsonUtils.objectToJson(categories));
            log.info("article category has been cached");
        } else {
            categories = JsonUtils.jsonToList(categoriesJson, Category.class);
        }
        return categories;
    }

    @Override
    public List<CategoryVO> getCategoryListWithArticleCount() {

        List<CategoryVO> results = new ArrayList<>();
        String cacheKey = RedisConf.REDIS_ARTICLE_CATEGORY_WITH_ARTICLE_COUNT;
        String cache = redisOperator.get(cacheKey);
        if (StringUtils.isNotBlank(cache)) {
            results = JsonUtils.jsonToList(cache, CategoryVO.class);
        } else {
            List<Category> categories = queryCategoryList();
            // 查询每个类别下文章的数量
            for (Category category : categories) {
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                int categoryCount = queryEachCategoryArticleCount(category.getId());
                categoryVO.setEachCategoryArticleCount(categoryCount);
                results.add(categoryVO);
            }
            redisOperator.set(cacheKey, JsonUtils.objectToJson(results));
        }
        return results;
    }

    @Override
    public PagedGridResult getArticleListByTime(String yearAndMonth, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Article> articleList = articleMapper.getArticleByTime(yearAndMonth);
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);
        List<Article> paged = pageInfo.getList();
        List<ArticleArchiveVO> archiveVOList = new ArrayList<>();
        for (Article article : paged) {
            ArticleArchiveVO articleArchiveVO = new ArticleArchiveVO();
            articleArchiveVO.setArticleId(article.getId());
            BeanUtils.copyProperties(article, articleArchiveVO);
            List<Tag> tagList = getTagList(article);
            articleArchiveVO.setTagList(tagList);
            archiveVOList.add(articleArchiveVO);
        }
        PagedGridResult pgr = new PagedGridResult();
        pgr.setRows(archiveVOList);
        pgr.setCurrentPage(pageInfo.getPageNum());
        pgr.setRecords(pageInfo.getTotal());
        pgr.setTotalPage(pageInfo.getPages());
        return pgr;
    }

    @Override
    public List<String> queryArchiveTimeList(String userId) {
        return articleMapper.queryArchiveTimeList(userId);
    }

    @Override
    public PagedGridResult timeline(String userId, Integer page, Integer pageSize) {
        return getPagedGridResult(userId, page, pageSize);
    }

    @Override
    public PagedGridResult queryArticleListByCategoryId(String userId, String categoryId, Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExample(example);
        criteria.andEqualTo(SysConf.AUTHOR_ID, userId);
        criteria.andEqualTo(SysConf.CATEGORY_ID, categoryId);
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        PageInfo<Article> pageInfo = new PageInfo<>(list);
        List<Article> paged = pageInfo.getList();
        List<ArticleClassifyVO> articleClassifyVOList = new ArrayList<>();
        for (Article article : paged) {
            ArticleClassifyVO articleClassifyVO = new ArticleClassifyVO();
            List<Tag> tagList = getTagList(article);
            BeanUtils.copyProperties(article, articleClassifyVO);
            articleClassifyVO.setTagList(tagList);
            articleClassifyVO.setArticleId(article.getId());
            articleClassifyVOList.add(articleClassifyVO);
        }
        PagedGridResult pgr = new PagedGridResult();
        pgr.setRows(articleClassifyVOList);
        pgr.setCurrentPage(pageInfo.getPageNum());
        pgr.setRecords(pageInfo.getTotal());
        pgr.setTotalPage(pageInfo.getPages());
        return pgr;
    }
}
