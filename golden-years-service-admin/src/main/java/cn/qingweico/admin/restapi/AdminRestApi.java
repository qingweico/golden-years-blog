package cn.qingweico.admin.restapi;

import cn.qingweico.admin.clients.FaceBase64Client;
import cn.qingweico.admin.service.AdminService;
import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConf;
import cn.qingweico.global.SysConf;
import cn.qingweico.pojo.bo.UpdatePwdBO;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Admin;
import cn.qingweico.pojo.bo.OperatorAdminBO;
import cn.qingweico.util.PagedGridResult;
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
@Api(value = "管理员信息相关的接口定义", tags = {"管理员信息相关的接口定义"})
@RequestMapping("admin")
@RestController
public class AdminRestApi extends BaseRestApi {
    @Resource
    private AdminService adminService;
    @Resource
    private FaceBase64Client client;

    @ApiOperation(value = "查询管理员用户名是否存在", notes = "查询管理员用户名是否存在", httpMethod = "POST")
    @PostMapping("/present")
    public GraceJsonResult present(String username) {
        adminService.checkUsernameIsPresent(username);
        return GraceJsonResult.ok();
    }

    @ApiOperation(value = "创建新的管理员", notes = "创建新的管理员", httpMethod = "POST")
    @PostMapping("/add")
    public GraceJsonResult add(@RequestBody OperatorAdminBO operatorAdminBO) {
        // 若BO中base64不为空, 则代表人脸识别登陆, 否则需要用户名和密码
        if (StringUtils.isBlank(operatorAdminBO.getImg64())) {
            if (StringUtils.isBlank(operatorAdminBO.getPassword()) ||
                    StringUtils.isBlank(operatorAdminBO.getConfirmPassword())) {
                return GraceJsonResult.error(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
            }
        }

        // 若密码不为空, 则必须验证两次输入的密码是否一致
        if (StringUtils.isNotBlank(operatorAdminBO.getPassword())) {
            if (!operatorAdminBO.getPassword().
                    equals(operatorAdminBO.getConfirmPassword())) {
                return GraceJsonResult.error(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);

            }
        }
        // 校验用户名唯一性
        adminService.checkUsernameIsPresent(operatorAdminBO.getUsername());
        // 执行admin信息入库操作
        adminService.createAdminUser(operatorAdminBO);
        return new GraceJsonResult(ResponseStatusEnum.APPEND_SUCCESS);
    }


    @ApiOperation(value = "查询管理员列表", notes = "查询管理员列表", httpMethod = "GET")
    @GetMapping("/list")
    public GraceJsonResult list(@ApiParam(name = "page", value = "分页查询下一页的第几页") @RequestParam Integer page,
                                @ApiParam(name = "pageSize", value = "分页查询每一页显示的条数") @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedGridResult res = adminService.queryAdminList(page, pageSize);
        return GraceJsonResult.ok(res);
    }

    @ApiOperation(value = "更改管理员账户密码", notes = "更改管理员账户密码", httpMethod = "POST")
    @PostMapping("/updatePwd")
    public GraceJsonResult updatePwd(@RequestBody @Valid UpdatePwdBO updatePwdBO) {
        String tokenKey = RedisConf.REDIS_ADMIN_TOKEN;
        String infoKey = RedisConf.REDIS_ADMIN_INFO;
        Admin loginUser = getLoginUser(Admin.class, tokenKey, infoKey);
        String id = loginUser.getId();
        if (!BCrypt.checkpw(updatePwdBO.getOldPassword(), loginUser.getPassword())) {
            GraceException.error(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
        }
        String encryptedPwd = BCrypt.hashpw(updatePwdBO.getNewPassword(), BCrypt.gensalt());
        adminService.alterPwd(id, encryptedPwd);
        return new GraceJsonResult(ResponseStatusEnum.ALERT_SUCCESS);
    }

    @ApiOperation(value = "更新管理员个人信息", notes = "更新管理员个人信息", httpMethod = "POST")
    @PostMapping("/updateProfile")
    public GraceJsonResult updateProfile(@RequestBody OperatorAdminBO user) {
        String tokenKey = RedisConf.REDIS_ADMIN_TOKEN;
        String infoKey = RedisConf.REDIS_ADMIN_INFO;
        Admin loginUser = getLoginUser(Admin.class, tokenKey, infoKey);
        String id = loginUser.getId();
        user.setId(id);
        // TODO 检擦手机号码和邮箱是否唯一
        adminService.updateUserProfile(user);
        return new GraceJsonResult(ResponseStatusEnum.UPDATE_SUCCESS);
    }
    @ApiOperation(value = "删除管理员人脸信息", notes = "删除管理员人脸信息", httpMethod = "POST")
    @PostMapping("/deleteFaceInfo/{id}")
    public GraceJsonResult deleteFaceInfo(@PathVariable("id") String id){
        Admin admin = adminService.queryAdminById(id);
        client.removeGridFsFile(admin.getFaceId());
        admin.setFaceId(SysConf.EMPTY_STRING);
        OperatorAdminBO operatorAdminBO = new OperatorAdminBO();
        BeanUtils.copyProperties(admin, operatorAdminBO);
        adminService.updateUserProfile(operatorAdminBO);
        return GraceJsonResult.ok();
    }
}
