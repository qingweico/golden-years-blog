package cn.qingweico.admin.controller;

import cn.qingweico.admin.clients.FaceBase64Client;
import cn.qingweico.admin.service.AdminUserService;
import cn.qingweico.api.controller.BaseController;
import cn.qingweico.api.controller.admin.AdminUserControllerApi;
import cn.qingweico.enums.FaceVerifyType;
import cn.qingweico.exception.GraceException;
import cn.qingweico.grace.result.GraceJsonResult;
import cn.qingweico.grace.result.ResponseStatusEnum;
import cn.qingweico.pojo.AdminUser;
import cn.qingweico.pojo.bo.AdminLoginBO;
import cn.qingweico.pojo.bo.NewAdminBO;
import cn.qingweico.util.FaceVerifyUtils;
import cn.qingweico.util.PagedGridResult;
import com.google.code.kaptcha.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author:qiming
 * @date: 2021/9/9
 */
@RestController
public class AdminUserController extends BaseController implements AdminUserControllerApi {
    @Resource
    private AdminUserService adminUserService;

    @Resource
    private FaceVerifyUtils faceVerify;

    @Resource
    private FaceBase64Client client;

    @Override
    public GraceJsonResult adminLogin(AdminLoginBO adminLoginBO,
                                      HttpServletRequest req,
                                      HttpServletResponse resp) {
        String actualVerificationCode = (String) req.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (!actualVerificationCode.equals(adminLoginBO.getVerifyCode())) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.VERIFICATION_CODE_ERROR);
        }
        AdminUser admin = adminUserService.queryAdminByUsername(adminLoginBO.getUsername());
        if (admin == null) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }
        boolean isPwdMatch = BCrypt.checkpw(adminLoginBO.getPassword(), admin.getPassword());
        if (isPwdMatch) {
            saveAdminInfo(admin, req, resp);
            return new GraceJsonResult(ResponseStatusEnum.LOGIN_SUCCESS);
        } else {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }
    }

    @Override
    public GraceJsonResult adminIsExist(String username) {
        checkAdminExist(username);
        return GraceJsonResult.ok();
    }

    @Override
    public GraceJsonResult addNewAdmin(NewAdminBO newAdminBO,
                                       HttpServletRequest req,
                                       HttpServletResponse resp) {

        // 判断BO中的用户名和密码不为空

        // 若BO中base64不为空 则代表人脸识别登陆 否则需要用户名和密码
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
    public GraceJsonResult getAdminList(Integer page, Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult res = adminUserService.queryAdminList(page, pageSize);

        return GraceJsonResult.ok(res);
    }

    @Override
    public GraceJsonResult adminLogout(String adminId,
                                       HttpServletRequest req,
                                       HttpServletResponse resp) {
        redisOperator.del(REDIS_ADMIN_TOKEN + ":" + adminId);
        setCookie(req, resp, "admin_token", "", COOKIE_DELETE);
        setCookie(req, resp, "admin_name", "", COOKIE_DELETE);
        setCookie(req, resp, "admin_id", "", COOKIE_DELETE);
        return GraceJsonResult.ok();
    }

    @Override
    public GraceJsonResult adminFaceLogin(AdminLoginBO adminLoginBO,
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
        String base64DB = null;
        if (result != null) {
            base64DB = (String) result.getData();
        }

        // 调用阿里ai进行人脸对比识别 判断可行度 从而实现人脸登陆
        boolean pass = faceVerify.faceVerify(FaceVerifyType.BASE64.type, base64, base64DB, 60);

        if (!pass) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);
        }
        // admin登陆后的数据设置 redis和cookie
        saveAdminInfo(adminUser, req, resp);
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
    private void saveAdminInfo(AdminUser admin,
                               HttpServletRequest req,
                               HttpServletResponse resp) {
        // 保存token到redis中
        String token = UUID.randomUUID().toString();
        redisOperator.set(REDIS_ADMIN_TOKEN + ":" + admin.getId(), token);

        // 保存admin登陆基本token信息到cookie中
        setCookie(req, resp, "admin_token", token, COOKIE_EXPIRE);
        setCookie(req, resp, "admin_id", admin.getId(), COOKIE_EXPIRE);
        setCookie(req, resp, "admin_name", admin.getUsername(), COOKIE_EXPIRE);
    }
}
