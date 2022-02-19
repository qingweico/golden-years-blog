package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.CategoryMapper;
import cn.qingweico.admin.service.CategoryService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.exception.GraceException;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
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
        int res = categoryMapper.insert(category);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        // 更新缓存
        redisOperator.del(REDIS_ALL_CATEGORY);
    }

    @Override
    public void modifyCategory(Category category) {
        int res = categoryMapper.updateByPrimaryKey(category);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        // 更新缓存
        redisOperator.del(REDIS_ALL_CATEGORY);
    }

    @Override
    public boolean queryCategoryIsExist(String categoryName, String oldCategoryName) {

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
    public List<Category> queryCategoryList() {
        return categoryMapper.selectAll();
    }
}
