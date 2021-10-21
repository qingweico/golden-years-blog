package cn.qingweico.admin.service;

import cn.qingweico.pojo.Category;

import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/10
 */
public interface CategoryService {
    /**
     * 新增文章分类
     */
    void createCategory(Category category);

    /**
     * 修改文章分类列表
     */
    void modifyCategory(Category category);

    /**
     * 查询分类名是否已经存在
     */
    boolean queryCategoryIsExist(String categoryName, String oldCategoryName);

    /**
     * 获得文章分类列表
     */
    List<Category> queryCategoryList();
}
