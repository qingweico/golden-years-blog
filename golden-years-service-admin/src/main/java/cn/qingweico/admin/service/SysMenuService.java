package cn.qingweico.admin.service;


import cn.qingweico.entity.SysMenu;

import java.util.List;

/**
 * @author zqw
 * @date 2022/3/23
 */
public interface SysMenuService {
    /**
     * 构建菜单树
     * @param sysMenuList List<SysMenu>
     * @return List<SysMenu>
     */
    List<SysMenu> buildTreeMenu(List<SysMenu> sysMenuList);

    /**
     * 根据菜单id获取某个菜单详情
     * @param id menu id
     * @return SysMenu
     */
    SysMenu getById(String id);


    /**
     * 新建菜单
     * @param sysMenu SysMenu
     */
    void createMenu(SysMenu sysMenu);

    /**
     * 编辑菜单
     * @param sysMenu SysMenu
     */
    void editMenu(SysMenu sysMenu);
}
