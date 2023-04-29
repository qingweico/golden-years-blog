package cn.qingweico.admin.service;


import cn.qingweico.entity.SysMenu;

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

    /**
     * 根据菜单id获取某个菜单详情
     * @param id menu id
     * @return
     */
    SysMenu getById(String id);


    /**
     * 新建菜单
     * @param sysMenu
     */
    void createMenu(SysMenu sysMenu);

    /**
     * 编辑菜单
     * @param sysMenu
     */
    void editMenu(SysMenu sysMenu);
}
