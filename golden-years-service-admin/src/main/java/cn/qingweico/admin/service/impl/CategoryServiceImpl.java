package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.clients.ArticleClient;
import cn.qingweico.admin.mapper.CategoryMapper;
import cn.qingweico.admin.service.CategoryService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.vo.CategoryVO;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Category;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Slf4j
@Service
public class CategoryServiceImpl extends BaseService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    private static List<CategoryVO> list;


    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void createCategory(Category category) {
        String id = sid.nextShort();
        category.setId(id);
        category.setStatus(YesOrNo.YES.type);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        if (categoryMapper.insert(category) > 0) {
            String k1 = RedisConf.REDIS_ARTICLE_CATEGORY;
            String k2 = RedisConf.REDIS_ARTICLE_CATEGORY_WITH_ARTICLE_COUNT;
            refreshCache(k1);
            refreshCache(k2);
        } else {
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void modifyCategory(Category category) {
        category.setUpdateTime(new Date());
        if (categoryMapper.updateByPrimaryKeySelective(category) > 0) {
            String k1 = RedisConf.REDIS_ARTICLE_CATEGORY;
            String k2 = RedisConf.REDIS_ARTICLE_CATEGORY_WITH_ARTICLE_COUNT;
            refreshCache(k1);
            refreshCache(k2);
        } else {
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

    }

    @Override
    public boolean queryCategoryIsPresent(String categoryName, String oldCategoryName) {

        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", categoryName);
        if (StringUtils.isNoneBlank(oldCategoryName)) {
            criteria.andNotEqualTo("name", oldCategoryName);
        }
        List<Category> categoryList = categoryMapper.selectByExample(example);
        return categoryList != null && !categoryList.isEmpty();
    }

    @Override
    public PagedGridResult queryCategoryList(String keyword, Boolean sort, Integer page, Integer pageSize) {
        Example example = new Example(Category.class);
        example.orderBy("createTime").desc();
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("name", "%" + keyword + "%");
        }
        PageHelper.startPage(page, pageSize);
        List<Category> list = categoryMapper.selectByExample(example);
        List<CategoryVO> result = new ArrayList<>();
        getCategoryListWithArticleCount();
        for (Category category : list) {
            CategoryVO categoryVO = new CategoryVO();
            BeanUtils.copyProperties(category, categoryVO);
            Integer articleCount = queryArticleCountByCategoryId(category.getId());
            categoryVO.setEachCategoryArticleCount(articleCount);
            result.add(categoryVO);
        }
        if (sort) {
            result.sort((a, b) -> b.getEachCategoryArticleCount() - a.getEachCategoryArticleCount());
        }
        return setterPagedGrid(result, page);
    }

    @Override
    public void deleteCategory(String categoryId) {
        if (categoryMapper.deleteByPrimaryKey(categoryId) > 0) {
            String key = RedisConf.REDIS_ARTICLE_CATEGORY;
            refreshCache(key);
        } else {
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteAll(List<String> ids) {
        for (String id : ids) {
            categoryMapper.deleteByPrimaryKey(id);
        }
    }

    @Resource
    private ArticleClient client;

    public void getCategoryListWithArticleCount() {
        Object data = client.getCategoryListWithArticleCount().getData();
        if (data != null) {
            String jsonData = JsonUtils.objectToJson(data);
            list = JsonUtils.jsonToList(jsonData, CategoryVO.class);
        }
    }

    public Integer queryArticleCountByCategoryId(String categoryId) {
        if (!list.isEmpty()) {
            for (CategoryVO categoryVo : list) {
                if (Objects.equals(categoryId, categoryVo.getId())) {
                    return categoryVo.getEachCategoryArticleCount();
                }
            }
        }
        return 0;
    }
}
