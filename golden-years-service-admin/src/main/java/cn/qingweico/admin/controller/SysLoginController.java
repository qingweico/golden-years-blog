package cn.qingweico.admin.controller;

import cn.qingweico.admin.clients.FaceBase64Client;
import cn.qingweico.admin.service.RoleService;
import cn.qingweico.admin.service.SysMenuService;
import cn.qingweico.admin.service.SysPermissionService;
import cn.qingweico.admin.service.SysUserService;
import cn.qingweico.admin.service.impl.SysLoginService;
import cn.qingweico.core.base.BaseController;
import cn.qingweico.entity.SysMenu;
import cn.qingweico.entity.SysUser;
import cn.qingweico.entity.model.LoginBody;
import cn.qingweico.enums.FaceVerifyType;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.util.*;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    @Resource
    private CompareFace faceVerify;

    @Resource
    private FaceBase64Client client;

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

    @ApiOperation(value = "人脸登陆", notes = "人脸登陆", httpMethod = "POST")
    @PostMapping("/face")
    public Result face(@RequestBody LoginBody loginBody) {
        // 判断用户名和faceId不为空
        if (StringUtils.isBlank(loginBody.getUsername())) {
            return Result.r(Response.USERNAME_EMPTY_ERROR);
        }
        SysUser sysUser = sysUserService.querySysUserByUsername(loginBody.getUsername());
        if (sysUser == null) {
            return Result.r(Response.SYS_USER_NOT_EXIT);
        }
        String base64 = loginBody.getImg64();
        if (StringUtils.isBlank(base64)) {
            return Result.r(Response.FACE_EMPTY_ERROR);
        }
        // 从数据库中查询faceId
        String faceId = sysUser.getFaceId();
        if (StringUtils.isBlank(faceId)) {
            return Result.r(Response.FACE_LOGIN_NOT_ENABLE);
        }
        // 请求文件服务, 获得人脸数据的base64数据
        Result result = client.getFaceBase64(faceId);
        String base64Db = null;
        if (result != null) {
            base64Db = (String) result.getData();
        }
        // 调用阿里AI进行人脸对比识别, 判断可行度, 从而实现人脸登陆
        boolean pass = faceVerify.faceVerify(FaceVerifyType.BASE64.getVal(), base64, base64Db, 60.0f);

        if (!pass) {
            return Result.r(Response.FACE_LOGIN_ERROR);
        }
        String sysUserId = sysUser.getId();
        String jsonWebToken = JwtUtils.createJwt(sysUserId);
        sysUserService.doSaveLoginLog(sysUserId);
        sysUserService.doSaveToken(sysUser, jsonWebToken);
        return Result.ok(Response.FACE_PASS, jsonWebToken);
    }
}
