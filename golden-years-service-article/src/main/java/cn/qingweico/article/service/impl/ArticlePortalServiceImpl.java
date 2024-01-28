package cn.qingweico.article.service.impl;

import cn.qingweico.core.service.BaseService;
import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.mapper.CategoryMapper;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.entity.Article;
import cn.qingweico.entity.Category;
import cn.qingweico.entity.model.ArticleArchive;
import cn.qingweico.entity.model.ArticleClassify;
import cn.qingweico.entity.model.ArticleDetail;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.redis.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
    private RedisCache redisCache;

    @Override
    public PagedResult queryPortalArticleList(String keyword, String category, String tag, Integer page, Integer pageSize) {

        LambdaQueryWrapper<Article> lwq = new LambdaQueryWrapper<>();
        setDefaultArticleCondition(lwq);
        if (StringUtils.isNotEmpty(keyword)) {
            lwq.like(Article::getTitle, SysConst.DELIMITER_PERCENT + keyword + SysConst.DELIMITER_PERCENT);
        }
        if (StringUtils.isNotEmpty(category)) {
            lwq.eq(Article::getCategoryId, category);
        }
        List<Article> list = articleMapper.selectList(lwq);
        return setterPagedGrid(list, page);
    }

    @Override
    public List<Article> queryHotArticle() {
        LambdaQueryWrapper<Article> lwq = new LambdaQueryWrapper<>();
        lwq.eq(Article::getIsDelete, YesOrNo.NO.getVal());
        lwq.eq(Article::getArticleType, ArticleReviewStatus.APPROVED.getVal());
        // TODO 分页
        return articleMapper.selectList(lwq);
    }

    @Override
    public PagedResult queryArticleListOfAuthor(String author, Integer page, Integer pageSize) {

        return getPagedGridResult(author, page, pageSize);
    }

    private PagedResult getPagedGridResult(String author, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Article> lwq = new LambdaQueryWrapper<>();
        setDefaultArticleCondition(lwq);
        lwq.eq(Article::getAuthorId, author);
        // TODO 分页
        List<Article> list = articleMapper.selectList(lwq);
        return setterPagedGrid(list, page);
    }

    @Override
    public List<Article> queryGoodArticleListOfAuthor(String author) {
        LambdaQueryWrapper<Article> lwq = new LambdaQueryWrapper<>();
        lwq.eq(Article::getIsDelete, YesOrNo.NO.getVal());
        lwq.eq(Article::getArticleType, ArticleReviewStatus.APPROVED.getVal());
        lwq.eq(Article::getAuthorId, author);
        return articleMapper.selectList(lwq);
    }

    @Override
    public ArticleDetail queryDetail(String articleId) {
        String cachedArticleDetail = redisCache.get(RedisConst.REDIS_ARTICLE_DETAIL + SysConst.SYMBOL_COLON + articleId);
        ArticleDetail articleDetail = null;
        if (StringUtils.isNotBlank(cachedArticleDetail)) {
            articleDetail = JsonUtils.jsonToPojo(cachedArticleDetail, ArticleDetail.class);
        } else {
            LambdaQueryWrapper<Article> lwq = new LambdaQueryWrapper<>();
            lwq.eq(Article::getId, articleId);
            lwq.eq(Article::getIsDelete, YesOrNo.NO.getVal());
            lwq.eq(Article::getIsAppoint, YesOrNo.NO.getVal());
            lwq.eq(Article::getArticleStatus, ArticleReviewStatus.APPROVED.getVal());
            Article result = articleMapper.selectOne(lwq);
            if (result != null) {
                articleDetail = new ArticleDetail();
                BeanUtils.copyProperties(result, articleDetail);
                redisCache.set(RedisConst.REDIS_ARTICLE_DETAIL + SysConst.SYMBOL_COLON + articleId, JsonUtils.objectToJson(articleDetail));
            }
        }
        return articleDetail;
    }

    @Override
    public Integer queryEachCategoryArticleCount(String categoryId) {
        LambdaQueryWrapper<Article> lwq = new LambdaQueryWrapper<>();
        lwq.eq(Article::getCategoryId, categoryId);
        return articleMapper.selectCount(lwq);
    }

    private void setDefaultArticleCondition(LambdaQueryWrapper<Article> lwq) {
        lwq.orderByDesc(Article::getCreateTime);
        lwq.eq(Article::getIsDelete, YesOrNo.NO.getVal());
        lwq.eq(Article::getIsAppoint, YesOrNo.NO.getVal());
        lwq.eq(Article::getArticleStatus, ArticleReviewStatus.APPROVED.getVal());
    }

    @Override
    public List<Category> queryCategoryList() {
        // 缓存
        String categoriesJson = redisCache.get(RedisConst.REDIS_ARTICLE_CATEGORY);
        List<Category> categories;
        if (StringUtils.isBlank(categoriesJson)) {
            categories = categoryMapper.selectList(null);
            redisCache.set(RedisConst.REDIS_ARTICLE_CATEGORY, JsonUtils.objectToJson(categories));
        } else {
            categories = JsonUtils.jsonToList(categoriesJson, Category.class);
        }
        return categories;
    }

    @Override
    public PagedResult getArticleListByTime(String yearAndMonth, Integer page, Integer pageSize) {
        // 分页
        List<Article> articleList = articleMapper.getArticleByTime(yearAndMonth);
        List<ArticleArchive> result = new ArrayList<>();
        for (Article article : articleList) {
            ArticleArchive articleArchive = new ArticleArchive();
            articleArchive.setArticleId(article.getId());
            BeanUtils.copyProperties(article, articleArchive);
            result.add(articleArchive);
        }
        System.out.println(result);
        return new PagedResult();
    }

    @Override
    public List<String> queryArchiveTimeList(String userId) {
        return articleMapper.queryArchiveTimeList(userId);
    }

    @Override
    public PagedResult timeline(String userId, Integer page, Integer pageSize) {
        return getPagedGridResult(userId, page, pageSize);
    }

    @Override
    public PagedResult queryArticleListByCategoryId(String userId, String categoryId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Article> lwq = new LambdaQueryWrapper<>();
        setDefaultArticleCondition(lwq);
        lwq.eq(Article::getAuthorId, userId);
        lwq.eq(Article::getCategoryId, categoryId);
        // 分页
        List<Article> list = articleMapper.selectList(lwq);
        List<ArticleClassify> articleClassifies = new ArrayList<>();
        for (Article article : list) {
            ArticleClassify articleClassify = new ArticleClassify();
            BeanUtils.copyProperties(article, articleClassify);
            articleClassify.setArticleId(article.getId());
            articleClassifies.add(articleClassify);
        }
        return new PagedResult();
    }
}
