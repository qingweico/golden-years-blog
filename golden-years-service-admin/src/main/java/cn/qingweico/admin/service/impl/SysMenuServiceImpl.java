package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.SysMenuMapper;
import cn.qingweico.admin.service.SysMenuService;
import cn.qingweico.entity.SysMenu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zqw
 * @date 2022/3/23
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {
    @Resource
    private SysMenuMapper sysMenuMapper;

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
        return sysMenuMapper.selectById(id);
    }

    @Override
    public void createMenu(SysMenu sysMenu) {

    }

    @Override
    public void editMenu(SysMenu sysMenu) {

    }
}
