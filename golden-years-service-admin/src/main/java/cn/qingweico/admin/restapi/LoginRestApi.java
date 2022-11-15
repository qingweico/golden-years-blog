package cn.qingweico.admin.restapi;

import cn.qingweico.admin.clients.FaceBase64Client;
import cn.qingweico.admin.service.AdminService;
import cn.qingweico.admin.service.CategoryMenuService;
import cn.qingweico.admin.service.RoleService;
import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.enums.FaceVerifyType;
import cn.qingweico.enums.MenuType;
import cn.qingweico.global.RedisConf;
import cn.qingweico.global.SysConf;
import cn.qingweico.pojo.Admin;
import cn.qingweico.pojo.CategoryMenu;
import cn.qingweico.pojo.Role;
import cn.qingweico.pojo.bo.AdminLoginBO;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.util.*;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.List;

/**
 * @author zqw
 * @date 2022/3/23
 */
@Slf4j
@Api(value = "管理员权限认证接口", tags = "管理员权限认证接口")
@RequestMapping("/auth")
@RestController
public class LoginRestApi extends BaseRestApi {
    @Resource
    private AdminService adminService;

    @Resource
    private RoleService roleService;

    @Resource
    private CategoryMenuService categoryMenuService;
    @Resource
    private CompareFace faceVerify;

    @Resource
    private FaceBase64Client client;

    @ApiOperation(value = "账户登陆", notes = "账户登陆", httpMethod = "POST")
    @PostMapping("/login")
    public Result login(@RequestBody AdminLoginBO adminLoginBO) {
        Admin admin = adminService.queryAdminByUsername(adminLoginBO.getUsername());
        if (admin == null) {
            return Result.r(Response.ADMIN_NOT_EXIT_ERROR);
        }
        boolean isPwdMatch = BCrypt.checkpw(adminLoginBO.getPassword(), admin.getPassword());
        if (isPwdMatch) {
            String adminId = admin.getId();
            String jsonWebToken = JwtUtils.createJwt(adminId);
            adminService.doSaveLoginLog(adminId);
            adminService.doSaveToken(admin, jsonWebToken);
            return Result.ok(Response.LOGIN_SUCCESS, jsonWebToken);
        } else {
            return Result.r(Response.ADMIN_NOT_EXIT_ERROR);
        }
    }

    @ApiOperation(value = "获取当前登陆用户的菜单", notes = "获取当前登陆用户的菜单")
    @GetMapping(value = "/getMenu")
    public Result getMenu(HttpServletRequest request) {

        Collection<CategoryMenu> categoryMenuList;
        String name = request.getParameter(SysConf.NAME);
        Admin admin = adminService.queryAdminByUsername(name);

        List<String> roleId = new ArrayList<>();
        roleId.add(admin.getRoleId());
        Collection<Role> roleList = roleService.listByIds(roleId);
        List<String> categoryMenuIdList = new ArrayList<>();
        roleList.forEach(item -> {
            String categoryMenuIds = item.getCategoryMenuIds();
            String[] ids = categoryMenuIds.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",");
            categoryMenuIdList.addAll(Arrays.asList(ids));
        });
        categoryMenuList = categoryMenuService.listByIds(categoryMenuIdList);
        Set<String> secondMenuIdList = new HashSet<>();
        categoryMenuList.forEach(item -> {
            // 查询二级菜单
            if (item.getMenuType().equals(MenuType.MENU.type) && item.getMenuLevel() == SysConf.NUM_TWO) {
                secondMenuIdList.add(item.getId());
            }
        });

        Collection<CategoryMenu> childCategoryMenuList = new ArrayList<>();
        Collection<CategoryMenu> parentCategoryMenuList = new ArrayList<>();
        Set<String> parentCategoryMenuIds = new HashSet<>();

        if (secondMenuIdList.size() > 0) {
            childCategoryMenuList = categoryMenuService.listByIds(secondMenuIdList);
        }

        childCategoryMenuList.forEach(item -> {
            // 选出所有的一级分类
            if (item.getMenuLevel() == SysConf.NUM_TWO) {
                if (StringUtils.isNotEmpty(item.getParentId())) {
                    parentCategoryMenuIds.add(item.getParentId());
                }
            }
        });

        if (parentCategoryMenuIds.size() > 0) {
            parentCategoryMenuList = categoryMenuService.listByIds(parentCategoryMenuIds);
        }

        List<CategoryMenu> list = new ArrayList<>(parentCategoryMenuList);
        Map<String, Object> map = new HashMap<>(SysConf.NUM_TWO);
        map.put(SysConf.PARENT_LIST, list);
        map.put(SysConf.SON_LIST, childCategoryMenuList);
        return Result.ok(map);
    }

    @ApiOperation(value = "获取当前登陆用户的信息", notes = "获取当前登陆用户的信息")
    @GetMapping(value = "/getInfo")
    public Result getInfo() {
        String tokenKey = RedisConf.REDIS_ADMIN_TOKEN;
        String infoKey = RedisConf.REDIS_ADMIN_INFO;
        return Result.ok(getLoginUser(Admin.class, tokenKey, infoKey));
    }

    @ApiOperation(value = "退出登陆", notes = "退出登陆", httpMethod = "POST")
    @PostMapping("/logout")
    public Result logout() {
        HttpServletRequest request = ServletReqUtils.getRequest();
        String token = request.getHeader("Authorization");
        String adminId = SysConf.EMPTY_STRING;
        try {
            Claims claims = JwtUtils.parseJwt(token);
            if (claims != null) {
                adminId = claims.get(SysConf.USER_ID, String.class);
            }
        } catch (Exception e) {
            return Result.r(Response.TICKET_INVALID);
        }
        redisTemplate.del(RedisConf.REDIS_ADMIN_INFO + SysConf.DELIMITER_COLON + adminId);
        redisTemplate.del(RedisConf.REDIS_ADMIN_TOKEN + SysConf.DELIMITER_COLON + adminId);
        return Result.r(Response.LOGOUT_SUCCESS);
    }

    @ApiOperation(value = "人脸登陆", notes = "人脸登陆", httpMethod = "POST")
    @PostMapping("/face")
    public Result face(@RequestBody AdminLoginBO adminLoginBO) {
        // 判断用户名和faceId不为空
        if (StringUtils.isBlank(adminLoginBO.getUsername())) {
            return Result.r(Response.ADMIN_USERNAME_NULL_ERROR);
        }
        Admin admin = adminService.queryAdminByUsername(adminLoginBO.getUsername());
        if (admin == null) {
            return Result.r(Response.ADMIN_IS_NOT_PRESENT);
        }
        String base64 = adminLoginBO.getImg64();
        if (StringUtils.isBlank(base64)) {
            return Result.r(Response.ADMIN_FACE_NULL_ERROR);
        }
        // 从数据库中查询faceId
        String adminFaceId = admin.getFaceId();
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
        String adminId = admin.getId();
        String jsonWebToken = JwtUtils.createJwt(adminId);
        adminService.doSaveLoginLog(adminId);
        adminService.doSaveToken(admin, jsonWebToken);
        return Result.ok(Response.FACE_PASS, jsonWebToken);
    }
}
