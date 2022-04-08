package cn.qingweico.admin.service;

import cn.qingweico.pojo.Category;
import cn.qingweico.util.PagedGridResult;

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
    boolean queryCategoryIsPresent(String categoryName, String oldCategoryName);

    /**
     * 获得文章分类列表
     * @param page 起始分页
     * @param pageSize 每页的数量
     * @return PagedGridResult
     */
    PagedGridResult queryCategoryList(Integer page, Integer pageSize);


    /**
     * 删除文章类别
     * @param categoryId 类别id
     */
    void deleteCategory(String categoryId);
}
