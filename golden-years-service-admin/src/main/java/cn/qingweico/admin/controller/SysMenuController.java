package cn.qingweico.admin.controller;

import cn.qingweico.admin.service.RoleService;
import cn.qingweico.admin.service.SysMenuService;
import cn.qingweico.admin.service.SysUserService;
import cn.qingweico.global.SysConst;
import cn.qingweico.pojo.Role;
import cn.qingweico.pojo.SysMenu;
import cn.qingweico.pojo.SysUser;
import cn.qingweico.result.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zqw
 * @date 2023/2/19
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private RoleService roleService;


    @ApiOperation(value = "获取当前登陆用户的菜单", notes = "获取当前登陆用户的菜单")
    @PreAuthorize("hasAuthority('system:menu:retrieve')")
    @GetMapping(value = "/getMenu")
    public Result getMenu(HttpServletRequest request) {
        String name = request.getParameter(SysConst.NAME);
        SysUser sysUser = sysUserService.querySysUserByUsername(name);
        if (StringUtils.isEmpty(sysUser.getId())) {
            return Result.fail();
        }
        // 根据用户id获取对应的角色
        Collection<Role> roles = roleService.queryRoleBySysUserId(sysUser.getId());
        List<String> ids = roles.stream().map(Role::getId).collect(Collectors.toList());
        // 根据角色id拿到可以操作的菜单
        List<SysMenu> sysMenuList = sysMenuService.queryMenuByRoleId(ids);
        // 构建菜单树 返回前端
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("treeMenu", sysMenuService.buildTreeMenu(sysMenuList));
        return Result.ok(map);
    }

    /**
     * 根据id查询菜单
     *
     * @param id menu id
     * @return Result
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Result findById(@PathVariable(value = "id") String id) {
        SysMenu sysMenu = sysMenuService.getById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("sysMenu", sysMenu);
        return Result.ok(map);
    }

    /**
     * 添加或者修改
     *
     * @param sysMenu SysMenu
     * @return Result
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:menu:add')" + "||" + "hasAuthority('system:menu:edit')")
    public Result save(SysMenu sysMenu) {
        if (StringUtils.isEmpty(sysMenu.getId())) {
            sysMenuService.createMenu(sysMenu);
        } else {
            sysMenuService.editMenu(sysMenu);
        }
        // 清除cache
        return Result.ok();
    }


    /**
     * 删除
     *
     * @param id 菜单id
     * @return Result
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result delete(@PathVariable(value = "id") String id) {
        return Result.ok();
    }
}
