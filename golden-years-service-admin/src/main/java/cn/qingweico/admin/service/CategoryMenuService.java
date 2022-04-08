package cn.qingweico.admin.service;

import cn.qingweico.pojo.CategoryMenu;

import java.util.Collection;
import java.util.List;

/**
 * @author zqw
 * @date 2022/3/23
 */
public interface CategoryMenuService {

    /**
     * 根据菜单id查询菜单分类内容
     *
     * @param categoryMenuId categoryMenuId
     * @return Collection<CategoryMenu>
     */
    List<CategoryMenu> listByIds(Collection<String> categoryMenuId);
}
