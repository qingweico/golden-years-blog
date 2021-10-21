package cn.qingweico.article.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.mapper.CategoryMapper;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/12
 */
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
    public PagedGridResult queryArticleListOfWriter(String writerId,
                                                    Integer page,
                                                    Integer pageSize) {

        Example example = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExample(example);
        criteria.andEqualTo("publishUserId", writerId);
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryGoodArticleListOfWriter(String writerId) {
        Example example = new Example(Article.class);
        example.orderBy("publishTime").desc();
        Example.Criteria criteria = setDefaultArticleExample(example);
        criteria.andEqualTo("publishUserId", writerId);
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

        ArticleDetailVO detailVO = new ArticleDetailVO();
        BeanUtils.copyProperties(result, detailVO);

        detailVO.setCover(result.getArticleCover());

        return detailVO;
    }

    @Override
    public Integer queryEachCategoryCount(Integer categoryId) {
        Article article = new Article();
        article.setCategoryId(categoryId);
        articleMapper.select(article);
        return articleMapper.selectCount(article);
    }

    private Example.Criteria setDefaultArticleExample(Example example) {
        example.orderBy("publishTime").desc();
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        criteria.andEqualTo("isAppoint", YesOrNo.NO.type);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);
        return criteria;
    }
    @Override
    public List<Category> queryCategoryList() {
        return categoryMapper.selectAll();
    }
}
