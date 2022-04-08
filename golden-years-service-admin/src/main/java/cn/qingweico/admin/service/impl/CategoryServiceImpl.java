package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.CategoryMapper;
import cn.qingweico.admin.service.CategoryService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConf;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Category;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Service
public class CategoryServiceImpl extends BaseService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;


    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void createCategory(Category category) {
        String id = sid.nextShort();
        category.setId(id);
        category.setStatus(YesOrNo.YES.type);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        if (categoryMapper.insert(category) > 0) {
            String key = RedisConf.REDIS_ARTICLE_CATEGORY;
            refreshCache(key);
        } else {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void modifyCategory(Category category) {
        category.setUpdateTime(new Date());
        if (categoryMapper.updateByPrimaryKeySelective(category) > 0) {
            String key = RedisConf.REDIS_ARTICLE_CATEGORY;
            refreshCache(key);
        } else {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
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
    public PagedGridResult queryCategoryList(Integer page, Integer pageSize) {
        Example example = new Example(Category.class);
        example.orderBy("createTime").desc();
        PageHelper.startPage(page, pageSize);
        List<Category> list = categoryMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }

    @Override
    public void deleteCategory(String categoryId) {
        if (categoryMapper.deleteByPrimaryKey(categoryId) > 0) {
            String key = RedisConf.REDIS_ARTICLE_CATEGORY;
            refreshCache(key);
        } else {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }
}
