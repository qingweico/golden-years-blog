package cn.qingweico.admin.controller;

import cn.qingweico.admin.clients.FaceBase64Client;
import cn.qingweico.admin.service.SysUserService;
import cn.qingweico.api.base.BaseController;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.pojo.SysUser;
import cn.qingweico.pojo.bo.UpdatePwdBO;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.pojo.bo.OperatorSysUserBO;
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
    private SysUserService adminService;
    @Resource
    private FaceBase64Client client;

    @ApiOperation(value = "查询系统用户名是否存在", notes = "查询系统用户名是否存在", httpMethod = "POST")
    @PostMapping("/present")
    public Result present(String username) {
        adminService.checkUsernamePresent(username);
        return Result.ok();
    }

    @ApiOperation(value = "创建新的管理员", notes = "创建新的管理员", httpMethod = "POST")
    @PostMapping("/add")
    public Result add(@RequestBody OperatorSysUserBO operatorSysUserBO) {
        // 若BO中base64不为空, 则代表人脸识别登陆, 否则需要用户名和密码
        if (StringUtils.isBlank(operatorSysUserBO.getImg64())) {
            if (StringUtils.isBlank(operatorSysUserBO.getPassword()) ||
                    StringUtils.isBlank(operatorSysUserBO.getConfirmPassword())) {
                return Result.r(Response.ADMIN_PASSWORD_NULL_ERROR);
            }
        }

        // 若密码不为空, 则必须验证两次输入的密码是否一致
        if (StringUtils.isNotBlank(operatorSysUserBO.getPassword())) {
            if (!operatorSysUserBO.getPassword().
                    equals(operatorSysUserBO.getConfirmPassword())) {
                return Result.r(Response.ADMIN_PASSWORD_ERROR);

            }
        }
        // 校验用户名唯一性
        adminService.checkUsernamePresent(operatorSysUserBO.getUsername());
        // 执行admin信息入库操作
        adminService.createSysUser(operatorSysUserBO);
        return Result.r(Response.APPEND_SUCCESS);
    }


    @ApiOperation(value = "查询管理员列表", notes = "查询管理员列表", httpMethod = "GET")
    @GetMapping("/list")
    public Result list(@ApiParam(name = "page", value = "分页查询下一页的第几页") @RequestParam Integer page,
                       @ApiParam(name = "pageSize", value = "分页查询每一页显示的条数") @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedResult res = adminService.querySysUserList(page, pageSize);
        return Result.ok(res);
    }

    @ApiOperation(value = "更改管理员账户密码", notes = "更改管理员账户密码", httpMethod = "POST")
    @PostMapping("/updatePwd")
    public Result updatePwd(@RequestBody @Valid UpdatePwdBO updatePwdBO) {
        String tokenKey = RedisConst.REDIS_ADMIN_TOKEN;
        String infoKey = RedisConst.REDIS_ADMIN_INFO;
        SysUser loginUser = getLoginUser(SysUser.class, tokenKey, infoKey);
        String id = loginUser.getId();
        if (!BCrypt.checkpw(updatePwdBO.getOldPassword(), loginUser.getPassword())) {
            GraceException.error(Response.ADMIN_PASSWORD_ERROR);
        }
        String encryptedPwd = BCrypt.hashpw(updatePwdBO.getNewPassword(), BCrypt.gensalt());
        adminService.alterPwd(id, encryptedPwd);
        return Result.r(Response.ALERT_SUCCESS);
    }

    @ApiOperation(value = "更新管理员个人信息", notes = "更新管理员个人信息", httpMethod = "POST")
    @PostMapping("/updateProfile")
    public Result updateProfile(@RequestBody OperatorSysUserBO user) {
        String tokenKey = RedisConst.REDIS_ADMIN_TOKEN;
        String infoKey = RedisConst.REDIS_ADMIN_INFO;
        SysUser loginUser = getLoginUser(SysUser.class, tokenKey, infoKey);
        String id = loginUser.getId();
        user.setId(id);
        // TODO 检擦手机号码和邮箱是否唯一
        adminService.updateSysUserProfile(user);
        return Result.r(Response.UPDATE_SUCCESS);
    }
    @ApiOperation(value = "删除管理员人脸信息", notes = "删除管理员人脸信息", httpMethod = "POST")
    @PostMapping("/deleteFaceInfo/{id}")
    public Result deleteFaceInfo(@PathVariable("id") String id){
        SysUser sysUser = adminService.querySysUserById(id);
        client.removeGridFsFile(sysUser.getFaceId());
        sysUser.setFaceId(SysConst.EMPTY_STRING);
        OperatorSysUserBO operatorSysUserBO = new OperatorSysUserBO();
        BeanUtils.copyProperties(sysUser, operatorSysUserBO);
        adminService.updateSysUserProfile(operatorSysUserBO);
        return Result.ok();
    }
}
