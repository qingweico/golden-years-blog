package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.CategoryMapper;
import cn.qingweico.admin.service.CategoryService;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.entity.Category;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.SnowflakeIdWorker;
import cn.qingweico.util.redis.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 博客类别Service
 *
 * @author zqw
 * @date 2021/9/10
 */
@Slf4j
@Service
public class CategoryServiceImpl extends BaseService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private RedisUtil redisUtil;


    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void createCategory(Category category) {
        String id = SnowflakeIdWorker.nextId();
        category.setId(id);
        category.setStatus(YesOrNo.YES.getVal());
        category.setCreateTime(DateUtils.nowDateTime());
        category.setUpdateTime(DateUtils.nowDateTime());
        if (categoryMapper.insert(category) > 0) {
            String k1 = RedisConst.REDIS_ARTICLE_CATEGORY;
            String k2 = RedisConst.REDIS_ARTICLE_CATEGORY_WITH_ARTICLE_COUNT;
            redisUtil.clearCache(k1);
            redisUtil.clearCache(k2);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void modifyCategory(Category category) {
        category.setUpdateTime(DateUtils.nowDateTime());
        if (categoryMapper.updateById(category) > 0) {
            String k1 = RedisConst.REDIS_ARTICLE_CATEGORY;
            String k2 = RedisConst.REDIS_ARTICLE_CATEGORY_WITH_ARTICLE_COUNT;
            redisUtil.clearCache(k1);
            redisUtil.clearCache(k2);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }

    }

    @Override
    public boolean queryCategoryHasPresent(String categoryName, String oldCategoryName) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getName, categoryName);
        if (StringUtils.isNoneBlank(oldCategoryName)) {
            lqw.ne(Category::getName, oldCategoryName);
        }
        List<Category> categoryList = categoryMapper.selectList(lqw);
        return categoryList != null && !categoryList.isEmpty();
    }

    @Override
    public PagedResult queryCategoryList(String keyword, Boolean sort, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(Category::getCreateTime);
        if (StringUtils.isNotBlank(keyword)) {
            lqw.like(Category::getName, SysConst.DELIMITER_PERCENT + keyword + SysConst.DELIMITER_PERCENT);
        }
        List<Category> list = categoryMapper.selectList(lqw);
        return setterPagedGrid(list, page);
    }

    @Override
    public void deleteCategory(String categoryId) {
        if (categoryMapper.deleteById(categoryId) > 0) {
            String key = RedisConst.REDIS_ARTICLE_CATEGORY;
            redisUtil.clearCache(key);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteAll(List<String> ids) {
        for (String id : ids) {
            categoryMapper.deleteById(id);
        }
    }
}
