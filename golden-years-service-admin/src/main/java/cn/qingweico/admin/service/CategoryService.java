package cn.qingweico.admin.service;

import cn.qingweico.pojo.Category;

import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
public interface CategoryService {
    /**
     * 新增文章分类
     *
     * @param category 文章类别名称
     */
    void createCategory(Category category);

    /**
     * 修改文章分类列表
     *
     * @param category 文章类别名称
     */
    void modifyCategory(Category category);

    /**
     * 查询分类名是否已经存在
     *
     * @param categoryName    文章类别名称
     * @param oldCategoryName 旧的文章类别名称
     * @return boolean 分类名是否已经存在
     */
    boolean queryCategoryIsExist(String categoryName, String oldCategoryName);

    /**
     * 获得文章分类列表
     *
     * @return List<Category> 文章分类列表
     */
    List<Category> queryCategoryList();
}
