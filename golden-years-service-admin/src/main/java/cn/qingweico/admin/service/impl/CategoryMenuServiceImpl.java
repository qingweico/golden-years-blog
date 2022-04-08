package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.CategoryMenuMapper;
import cn.qingweico.admin.service.CategoryMenuService;
import cn.qingweico.global.Constants;
import cn.qingweico.pojo.CategoryMenu;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @author zqw
 * @date 2022/3/23
 */
@Service
public class CategoryMenuServiceImpl implements CategoryMenuService {
    @Resource
    private CategoryMenuMapper categoryMenuMapper;

    @Override
    public List<CategoryMenu> listByIds(Collection<String> categoryMenuIds) {
        List<CategoryMenu> categoryMenuList = new ArrayList<>();
        for (String categoryMenuId : categoryMenuIds) {
            CategoryMenu categoryMenu = categoryMenuMapper.selectByPrimaryKey(categoryMenuId);
            categoryMenuList.add(categoryMenu);
        }
        return categoryMenuList;
    }
}
