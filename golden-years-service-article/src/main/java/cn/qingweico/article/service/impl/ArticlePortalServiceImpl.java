package cn.qingweico.article.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.mapper.CategoryMapper;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
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

    @Override
    public PagedGridResult queryPortalArticleList(String keyword,
                                                  Integer category,
                                                  Integer page,
                                                  Integer pageSize) {


        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExample(example);

        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("title", "%" + keyword + "%");
        }
        if (category != null) {
            criteria.andEqualTo("categoryId", category);
        }
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }

    @Override
    public List<Article> queryHotNews() {
        Example example = new Example(Article.class);
        setDefaultArticleExample(example);
        PageHelper.startPage(1, 5);
        return articleMapper.selectByExample(example);
    }

    @Override
    public PagedGridResult queryArticleListOfAuthor(String author,
                                                    Integer page,
                                                    Integer pageSize) {

        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExample(example);
        criteria.andEqualTo("author", author);
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryGoodArticleListOfAuthor(String author) {
        Example example = new Example(Article.class);
        example.orderBy("createTime").desc();
        Example.Criteria criteria = setDefaultArticleExample(example);
        criteria.andEqualTo("author", author);
        PageHelper.startPage(1, 5);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, 1);
    }

    @Override
    public ArticleDetailVO queryDetail(String articleId) {

        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(YesOrNo.NO.type);
        article.setIsDelete(YesOrNo.NO.type);
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.type);

        Article result = articleMapper.selectOne(article);

        if (result != null) {
            ArticleDetailVO detailVO = new ArticleDetailVO();
            BeanUtils.copyProperties(result, detailVO);
            return detailVO;
        }
        return null;
    }

    @Override
    public Integer queryEachCategoryCount(Integer categoryId) {
        Article article = new Article();
        article.setCategoryId(categoryId);
        articleMapper.select(article);
        return articleMapper.selectCount(article);
    }

    private Example.Criteria setDefaultArticleExample(Example example) {
        example.orderBy("createTime").desc();
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        criteria.andEqualTo("isAppoint", YesOrNo.NO.type);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);
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
            log.info("类别信息已存入缓存");
        } else {
            categories = JsonUtils.jsonToList(categoriesJson, Category.class);
        }
        return categories;
    }

    @Override
    public List<Article> getArticleListByTime(String yearAndMonth, Integer page, Integer pageSize) {
        return articleMapper.getArticleByTime(yearAndMonth, page, pageSize);
    }

    @Override
    public List<String> queryArchiveTimeList(String userId) {
        return articleMapper.queryArchiveTimeList(userId);
    }

    @Override
    public PagedGridResult timeline(String userId, Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExample(example);
        criteria.andEqualTo("authorId", userId);

        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryArticleListByCategoryId(String userId, Integer categoryId, Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExample(example);
        criteria.andEqualTo("authorId", userId);
        criteria.andEqualTo("categoryId", categoryId);
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }
}
