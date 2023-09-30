package cn.qingweico.admin.controller;

import cn.qingweico.admin.clients.FaceBase64Client;
import cn.qingweico.admin.service.SysUserService;
import cn.qingweico.core.base.BaseController;
import cn.qingweico.entity.SysUser;
import cn.qingweico.entity.model.OperatorSysUser;
import cn.qingweico.entity.model.UpdatePassword;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;

import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.util.PagedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author zqw
 * @date 2021/9/9
 */
@Api(value = "系统用户相关的接口定义", tags = {"系统用户相关的接口定义"})
@RequestMapping("sys/user")
@RestController
public class SysUserController extends BaseController {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FaceBase64Client client;

    @ApiOperation(value = "查询系统用户名是否存在", notes = "查询系统用户名是否存在", httpMethod = "POST")
    @PostMapping("/present")
    public Result present(String username) {
        sysUserService.checkUsernamePresent(username);
        return Result.ok();
    }

    @ApiOperation(value = "创建新的系统用户", notes = "创建新的系统用户", httpMethod = "POST")
    @PostMapping("/add")
    public Result add(@RequestBody OperatorSysUser operatorSysUser) {
        // 若BO中base64不为空, 则代表人脸识别登陆, 否则需要用户名和密码
        if (StringUtils.isBlank(operatorSysUser.getImg64())) {
            if (StringUtils.isBlank(operatorSysUser.getPassword()) ||
                    StringUtils.isBlank(operatorSysUser.getConfirmPassword())) {
                return Result.r(Response.ADMIN_PASSWORD_NULL_ERROR);
            }
        }

        // 若密码不为空, 则必须验证两次输入的密码是否一致
        if (StringUtils.isNotBlank(operatorSysUser.getPassword())) {
            if (!operatorSysUser.getPassword().
                    equals(operatorSysUser.getConfirmPassword())) {
                return Result.r(Response.ADMIN_PASSWORD_ERROR);

            }
        }
        // 校验用户名唯一性
        sysUserService.checkUsernamePresent(operatorSysUser.getUsername());
        sysUserService.createSysUser(operatorSysUser);
        return Result.r(Response.APPEND_SUCCESS);
    }


    @ApiOperation(value = "查询系统用户列表", notes = "查询系统用户列表", httpMethod = "GET")
    @GetMapping("/list")
    public Result list(@ApiParam(name = "page", value = "分页查询下一页的第几页") @RequestParam Integer page,
                       @ApiParam(name = "pageSize", value = "分页查询每一页显示的条数") @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedResult res = sysUserService.querySysUserList(page, pageSize);
        return Result.ok(res);
    }

    @ApiOperation(value = "更改系统用户账户密码", notes = "更改系统用户账户密码", httpMethod = "POST")
    @PostMapping("/updatePwd")
    public Result updatePwd(@RequestBody @Valid UpdatePassword updatePassword) {
        String tokenKey = RedisConst.REDIS_ADMIN_TOKEN;
        String infoKey = RedisConst.REDIS_ADMIN_INFO;
        SysUser loginUser = getLoginUser(SysUser.class, tokenKey, infoKey);
        String id = loginUser.getId();
        if (!BCrypt.checkpw(updatePassword.getOldPassword(), loginUser.getPassword())) {
            GraceException.error(Response.ADMIN_PASSWORD_ERROR);
        }
        String encryptedPwd = BCrypt.hashpw(updatePassword.getNewPassword(), BCrypt.gensalt());
        sysUserService.alterPwd(id, encryptedPwd);
        return Result.r(Response.ALERT_SUCCESS);
    }

    @ApiOperation(value = "更新系统用户账户密码个人信息", notes = "更新系统用户账户密码个人信息", httpMethod = "POST")
    @PostMapping("/updateProfile")
    public Result updateProfile(@RequestBody OperatorSysUser user) {
        String tokenKey = RedisConst.REDIS_ADMIN_TOKEN;
        String infoKey = RedisConst.REDIS_ADMIN_INFO;
        SysUser loginUser = getLoginUser(SysUser.class, tokenKey, infoKey);
        String id = loginUser.getId();
        user.setId(id);
        // TODO 检擦手机号码和邮箱是否唯一
        sysUserService.updateSysUserProfile(user);
        return Result.r(Response.UPDATE_SUCCESS);
    }
    @ApiOperation(value = "删除系统用户人脸信息", notes = "删除系统用户人脸信息", httpMethod = "POST")
    @PostMapping("/deleteFaceInfo/{id}")
    public Result deleteFaceInfo(@PathVariable("id") String id){
        SysUser sysUser = sysUserService.querySysUserById(id);
        client.removeGridFsFile(sysUser.getFaceId());
        sysUser.setFaceId(SysConst.EMPTY_STRING);
        OperatorSysUser operatorSysUserBO = new OperatorSysUser();
        BeanUtils.copyProperties(sysUser, operatorSysUserBO);
        sysUserService.updateSysUserProfile(operatorSysUserBO);
        return Result.ok();
    }
}
