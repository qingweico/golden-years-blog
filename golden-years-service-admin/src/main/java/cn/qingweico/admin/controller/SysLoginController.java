package cn.qingweico.admin.controller;

import cn.qingweico.admin.service.SysMenuService;
import cn.qingweico.admin.service.SysPermissionService;
import cn.qingweico.admin.service.SysUserService;
import cn.qingweico.admin.service.impl.SysLoginService;
import cn.qingweico.core.base.BaseController;
import cn.qingweico.entity.SysMenu;
import cn.qingweico.entity.SysUser;
import cn.qingweico.entity.model.LoginBody;
import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.security.utils.SecurityUtils;
import cn.qingweico.util.CollUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author zqw
 * @date 2022/3/23
 */
@Slf4j
@Api(value = "系统用户登录验证", tags = "系统用户登录验证")
@RestController
public class SysLoginController extends BaseController {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysLoginService loginService;

    @Resource
    private SysMenuService menuService;

    @Resource
    private SysPermissionService permissionService;


    @ApiOperation(value = "账户登陆", notes = "账户登陆", httpMethod = "POST")
    @PostMapping("/login")
    public Result login(@RequestBody LoginBody loginBody) {
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getUuid());
        return Result.ok(Response.LOGIN_SUCCESS, token);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @ApiOperation(value = "获取当前登陆用户的信息", notes = "获取当前登陆用户的信息")
    @GetMapping("getInfo")
    public Result getInfo() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        HashMap<String, Object> result = new HashMap<>(CollUtils.mapSize(3));
        result.put("user", user);
        result.put("roles", roles);
        result.put("permissions", permissions);
        return Result.ok(result);
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @ApiOperation(value = "获取路由信息", notes = "获取路由信息")
    @GetMapping("getRouters")
    public Result getRouters() {
        String userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return Result.ok(menuService.buildMenus(menus));
    }


}
