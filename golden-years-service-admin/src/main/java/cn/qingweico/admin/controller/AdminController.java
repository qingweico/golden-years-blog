package cn.qingweico.admin.controller;

import cn.qingweico.admin.clients.FaceBase64Client;
import cn.qingweico.admin.service.AdminUserService;
import cn.qingweico.api.controller.BaseController;
import cn.qingweico.api.controller.admin.AdminControllerApi;
import cn.qingweico.enums.FaceVerifyType;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.Constants;
import cn.qingweico.global.RedisConf;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.AdminUser;
import cn.qingweico.pojo.bo.AdminLoginBO;
import cn.qingweico.pojo.bo.NewAdminBO;
import cn.qingweico.util.FaceVerifyUtils;
import cn.qingweico.util.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author zqw
 * @date 2021/9/9
 */
@RestController
public class AdminController extends BaseController implements AdminControllerApi {
    @Resource
    private AdminUserService adminUserService;

    @Resource
    private FaceVerifyUtils faceVerify;

    @Resource
    private FaceBase64Client client;

    @Override
    public GraceJsonResult login(AdminLoginBO adminLoginBO,
                                 HttpServletRequest req,
                                 HttpServletResponse resp) {
//        String actualVerificationCode = (String) req.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
//        if (!actualVerificationCode.equals(adminLoginBO.getVerifyCode())) {
//            return GraceJsonResult.errorCustom(ResponseStatusEnum.VERIFICATION_CODE_ERROR);
//        }
        AdminUser admin = adminUserService.queryAdminByUsername(adminLoginBO.getUsername());
        if (admin == null) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }
        boolean isPwdMatch = BCrypt.checkpw(adminLoginBO.getPassword(), admin.getPassword());
        if (isPwdMatch) {
            doSaveAdminInfo(admin, req, resp);
            return new GraceJsonResult(ResponseStatusEnum.LOGIN_SUCCESS);
        } else {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }
    }

    @Override
    public GraceJsonResult present(String username) {
        checkAdminExist(username);
        return GraceJsonResult.ok();
    }

    @Override
    public GraceJsonResult add(NewAdminBO newAdminBO,
                               HttpServletRequest req,
                               HttpServletResponse resp) {

        // 判断BO中的用户名和密码不为空

        // 若BO中base64不为空, 则代表人脸识别登陆, 否则需要用户名和密码
        if (StringUtils.isBlank(newAdminBO.getImg64())) {
            if (StringUtils.isBlank(newAdminBO.getPassword()) ||
                    StringUtils.isBlank(newAdminBO.getConfirmPassword())) {
                return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
            }
        }

        // 若密码不为空, 则必须验证两次输入的密码是否一致
        if (StringUtils.isNotBlank(newAdminBO.getPassword())) {
            if (!newAdminBO.getPassword().
                    equals(newAdminBO.getConfirmPassword())) {
                return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);

            }
        }
        // 校验用户名唯一性
        checkAdminExist(newAdminBO.getUsername());

        // 执行admin信息入库操作
        adminUserService.createAdminUser(newAdminBO);

        return new GraceJsonResult(ResponseStatusEnum.APPEND_SUCCESS);
    }

    @Override
    public GraceJsonResult list(Integer page, Integer pageSize) {
        if (page == null) {
            page = Constants.COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = Constants.COMMON_PAGE_SIZE;
        }
        PagedGridResult res = adminUserService.queryAdminList(page, pageSize);

        return GraceJsonResult.ok(res);
    }

    @Override
    public GraceJsonResult logout(String adminId,
                                  HttpServletRequest req,
                                  HttpServletResponse resp) {
        redisOperator.del(RedisConf.REDIS_ADMIN_TOKEN + Constants.DELIMITER_COLON + adminId);
        return GraceJsonResult.ok();
    }

    @Override
    public GraceJsonResult face(AdminLoginBO adminLoginBO,
                                HttpServletRequest req,
                                HttpServletResponse resp) {
        // 判断用户名和faceId不为空
        if (StringUtils.isBlank(adminLoginBO.getUsername())) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_USERNAME_NULL_ERROR);
        }

        String base64 = adminLoginBO.getImg64();
        if (StringUtils.isBlank(base64)) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_NULL_ERROR);
        }
        // 从数据库中查询faceId
        AdminUser adminUser = adminUserService.queryAdminByUsername(adminLoginBO.getUsername());
        String adminFaceId = adminUser.getFaceId();
        if (StringUtils.isBlank(adminFaceId)) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);
        }

        // 请求文件服务 获得人脸数据的base64数据
        GraceJsonResult result = client.getFaceBase64(adminFaceId);
        String base64Db = null;
        if (result != null) {
            base64Db = (String) result.getData();
        }

        // 调用阿里ai进行人脸对比识别 判断可行度 从而实现人脸登陆
        boolean pass = faceVerify.faceVerify(FaceVerifyType.BASE64.type, base64, base64Db, 60);

        if (!pass) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);
        }
        // admin登陆后的数据设置redis和cookie
        doSaveAdminInfo(adminUser, req, resp);
        return GraceJsonResult.ok();
    }

    private void checkAdminExist(String username) {
        AdminUser admin = adminUserService.queryAdminByUsername(username);
        if (admin != null) {
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
    }

    /**
     * 登陆成功后保存admin有关信息
     *
     * @param admin AdminUser
     * @param req   HttpServletRequest
     * @param resp  HttpServletResponse
     */
    private void doSaveAdminInfo(AdminUser admin,
                                 HttpServletRequest req,
                                 HttpServletResponse resp) {
        // 保存token到redis中
        String token = UUID.randomUUID().toString();
        redisOperator.set(RedisConf.REDIS_ADMIN_TOKEN + Constants.SYMBOL_COLON + admin.getId(), token);
    }
}
