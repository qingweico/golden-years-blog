package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.RoleMenuRelMapper;
import cn.qingweico.admin.mapper.SysMenuMapper;
import cn.qingweico.admin.service.SysMenuService;
import cn.qingweico.pojo.SysMenu;
import cn.qingweico.pojo.RoleMenuRel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zqw
 * @date 2022/3/23
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {
    @Resource
    private SysMenuMapper sysMenuMapper;
    @Resource
    private RoleMenuRelMapper roleMenuRelMapper;

    @Override
    public List<SysMenu> queryMenuByRoleId(List<String> roles) {
        RoleMenuRel roleMenuRel = new RoleMenuRel();
        if (!CollectionUtils.isEmpty(roles)) {
            List<String> sysMenuIds = new ArrayList<>();
            for (String roleId : roles) {
                roleMenuRel.setRoleId(roleId);
                List<RoleMenuRel> roleMenuRelList = roleMenuRelMapper.select(roleMenuRel);
                for (RoleMenuRel roleMenu : roleMenuRelList) {
                    sysMenuIds.add(roleMenu.getMenuId());
                }
            }
            List<SysMenu> sysMenuList = new ArrayList<>();
            for (String sysMenuId : sysMenuIds) {
                SysMenu sysMenu = sysMenuMapper.selectByPrimaryKey(sysMenuId);
                sysMenuList.add(sysMenu);
            }
            return sysMenuList;
        }


        return new ArrayList<>(0);
    }

    @Override
    public List<SysMenu> buildTreeMenu(List<SysMenu> sysMenuList) {
        List<SysMenu> resultMenuList = new ArrayList<>();

        for (SysMenu sysMenu : sysMenuList) {
            // 寻找子节点
            for (SysMenu e : sysMenuList) {
                if (Objects.equals(e.getParentId(), sysMenu.getId())) {
                    sysMenu.getChildren().add(e);
                }
            }
            if (StringUtils.isEmpty(sysMenu.getParentId())) {
                resultMenuList.add(sysMenu);
            }
        }
        return resultMenuList;
    }

    @Override
    public SysMenu getById(String id) {
        return sysMenuMapper.selectByPrimaryKey(id);
    }

    @Override
    public void createMenu(SysMenu sysMenu) {

    }

    @Override
    public void editMenu(SysMenu sysMenu) {

    }
}
