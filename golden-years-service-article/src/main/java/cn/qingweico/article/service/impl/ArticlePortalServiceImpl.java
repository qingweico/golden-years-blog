package cn.qingweico.article.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.mapper.CategoryMapper;
import cn.qingweico.article.mapper.TagMapper;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.Tag;
import cn.qingweico.pojo.vo.ArticleArchiveVO;
import cn.qingweico.pojo.vo.ArticleClassifyVO;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.pojo.vo.IndexArticleVO;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
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
            criteria.andLike("title", "%" + keyword + "%");
        }
        if (StringUtils.isNotBlank(category)) {
            criteria.andEqualTo("categoryId", category);
        }
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);

        if (StringUtils.isNotBlank(tag)) {
            List<Article> result = new ArrayList<>();
            // 标签筛选
            for (Article article : list) {
                String tags = article.getTags();
                String[] tagIds = tags.replace("[", "")
                        .replace("]", "")
                        .replace("\"", "")
                        .split(",");
                for (String tagId : tagIds) {
                    if (tag.equals(tagId)) {
                        result.add(article);
                        break;
                    }
                }
                list = result;
            }
        }
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryHotArticle(Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        example.orderBy("influence").desc();
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
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
        criteria.andEqualTo("authorId", author);
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryGoodArticleListOfAuthor(String author) {
        Example example = new Example(Article.class);
        example.orderBy("influence").desc();
        Example.Criteria criteria = example.createCriteria();
        ;
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);
        criteria.andEqualTo("authorId", author);
        // 默认只展示5篇热门文章
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
            List<Tag> tagList = getTagList(result);
            ArticleDetailVO detailVO = new ArticleDetailVO();
            detailVO.setTagList(tagList);
            BeanUtils.copyProperties(result, detailVO);
            return detailVO;
        }
        return null;
    }

    private List<Tag> getTagList(Article article) {
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
    public List<ArticleArchiveVO> getArticleListByTime(String yearAndMonth, Integer page, Integer pageSize) {
        List<Article> articleList = articleMapper.getArticleByTime(yearAndMonth, page, pageSize);
        List<ArticleArchiveVO> archiveVOList = new ArrayList<>();
        for (Article article : articleList) {
            ArticleArchiveVO articleArchiveVO = new ArticleArchiveVO();
            articleArchiveVO.setArticleId(article.getId());
            BeanUtils.copyProperties(article, articleArchiveVO);
            List<Tag> tagList = getTagList(article);
            articleArchiveVO.setTagList(tagList);
            archiveVOList.add(articleArchiveVO);
        }
        return archiveVOList;
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
    public PagedGridResult queryArticleListByCategoryId(String userId, Integer categoryId, Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExample(example);
        criteria.andEqualTo("authorId", userId);
        criteria.andEqualTo("categoryId", categoryId);
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        List<ArticleClassifyVO> articleClassifyVOList = new ArrayList<>();
        for (Article article : list) {
            ArticleClassifyVO articleClassifyVO = new ArticleClassifyVO();
            List<Tag> tagList = getTagList(article);
            BeanUtils.copyProperties(article, articleClassifyVO);
            articleClassifyVO.setTagList(tagList);
            articleClassifyVO.setArticleId(article.getId());
            articleClassifyVOList.add(articleClassifyVO);
        }
        return setterPagedGrid(articleClassifyVOList, page);
    }
}
