package cn.qingweico.admin.controller;

import cn.qingweico.admin.clients.FaceBase64Client;
import cn.qingweico.admin.service.RoleService;
import cn.qingweico.admin.service.SysMenuService;
import cn.qingweico.admin.service.SysUserService;
import cn.qingweico.core.base.BaseController;
import cn.qingweico.entity.SysUser;
import cn.qingweico.entity.model.LoginBody;
import cn.qingweico.enums.FaceVerifyType;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.pojo.bo.AdminLoginBO;
import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.util.CompareFace;
import cn.qingweico.util.JwtUtils;
import cn.qingweico.util.ServletReqUtils;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    private RoleService roleService;

    @Resource
    private SysMenuService categoryMenuService;
    @Resource
    private CompareFace faceVerify;

    @Resource
    private FaceBase64Client client;

    @ApiOperation(value = "账户登陆", notes = "账户登陆", httpMethod = "POST")
    @PostMapping("/login")
    public Result login(@RequestBody LoginBody loginBody) {
        SysUser sysUser = sysUserService.querySysUserByUsername(loginBody.getUsername());
        if (sysUser == null) {
            return Result.r(Response.SYS_USER_NOT_EXIT);
        }
        boolean isPwdMatch = BCrypt.checkpw(loginBody.getPassword(), sysUser.getPassword());
        if (isPwdMatch) {
            String adminId = sysUser.getId();
            String jsonWebToken = JwtUtils.createJwt(adminId);
            sysUserService.doSaveLoginLog(adminId);
            sysUserService.doSaveToken(sysUser, jsonWebToken);
            return Result.ok(Response.LOGIN_SUCCESS, jsonWebToken);
        } else {
            return Result.r(Response.SYS_USER_NOT_EXIT);
        }
    }


    @ApiOperation(value = "获取当前登陆用户的信息", notes = "获取当前登陆用户的信息")
    @GetMapping(value = "/getInfo")
    public Result getInfo() {
        String tokenKey = RedisConst.REDIS_ADMIN_TOKEN;
        String infoKey = RedisConst.REDIS_ADMIN_INFO;
        return Result.ok(getLoginUser(SysUser.class, tokenKey, infoKey));
    }

    @ApiOperation(value = "退出登陆", notes = "退出登陆", httpMethod = "POST")
    @PostMapping("/logout")
    public Result logout() {
        HttpServletRequest request = ServletReqUtils.getRequest();
        String token = request.getHeader("Authorization");
        String adminId = SysConst.EMPTY_STRING;
        try {
            Claims claims = JwtUtils.parseJwt(token);
            if (claims != null) {
                adminId = claims.get(SysConst.USER_ID, String.class);
            }
        } catch (Exception e) {
            return Result.r(Response.TICKET_INVALID);
        }
        redisCache.del(RedisConst.REDIS_ADMIN_INFO + SysConst.DELIMITER_COLON + adminId);
        redisCache.del(RedisConst.REDIS_ADMIN_TOKEN + SysConst.DELIMITER_COLON + adminId);
        return Result.r(Response.LOGOUT_SUCCESS);
    }

    @ApiOperation(value = "人脸登陆", notes = "人脸登陆", httpMethod = "POST")
    @PostMapping("/face")
    public Result face(@RequestBody AdminLoginBO adminLoginBO) {
        // 判断用户名和faceId不为空
        if (StringUtils.isBlank(adminLoginBO.getUsername())) {
            return Result.r(Response.ADMIN_USERNAME_NULL_ERROR);
        }
        SysUser sysUser = sysUserService.querySysUserByUsername(adminLoginBO.getUsername());
        if (sysUser == null) {
            return Result.r(Response.ADMIN_IS_NOT_PRESENT);
        }
        String base64 = adminLoginBO.getImg64();
        if (StringUtils.isBlank(base64)) {
            return Result.r(Response.ADMIN_FACE_NULL_ERROR);
        }
        // 从数据库中查询faceId
        String adminFaceId = sysUser.getFaceId();
        if (StringUtils.isBlank(adminFaceId)) {
            return Result.r(Response.ADMIN_FACE_LOGIN_NOT_ENABLE);
        }
        // 请求文件服务, 获得人脸数据的base64数据
        Result result = client.getFaceBase64(adminFaceId);
        String base64Db = null;
        if (result != null) {
            base64Db = (String) result.getData();
        }
        // 调用阿里ai进行人脸对比识别, 判断可行度, 从而实现人脸登陆
        boolean pass = faceVerify.faceVerify(FaceVerifyType.BASE64.type, base64, base64Db, 60.0f);

        if (!pass) {
            return Result.r(Response.ADMIN_FACE_LOGIN_ERROR);
        }
        String adminId = sysUser.getId();
        String jsonWebToken = JwtUtils.createJwt(adminId);
        sysUserService.doSaveLoginLog(adminId);
        sysUserService.doSaveToken(sysUser, jsonWebToken);
        return Result.ok(Response.FACE_PASS, jsonWebToken);
    }
}
