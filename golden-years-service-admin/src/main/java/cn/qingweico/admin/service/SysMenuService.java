package cn.qingweico.admin.service;

import cn.qingweico.pojo.SysMenu;

import java.util.Collection;
import java.util.List;

/**
 * @author zqw
 * @date 2022/3/23
 */
public interface SysMenuService {

    /**
     * 根据角色id查询菜单分类内容
     * @param roles 角色ids
     * @return List<SysMenu>
     */
    List<SysMenu> queryMenuByRoleId(List<String> roles);

    /**
     * 构建菜单树
     * @param sysMenuList List<SysMenu>
     * @return List<SysMenu>
     */
    List<SysMenu> buildTreeMenu(List<SysMenu> sysMenuList);

    SysMenu getById(String id);


    void createMenu(SysMenu sysMenu);

    void editMenu(SysMenu sysMenu);
}
