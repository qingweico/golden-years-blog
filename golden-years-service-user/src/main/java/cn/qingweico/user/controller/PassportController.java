package cn.qingweico.user.controller;

import cn.qingweico.api.controller.BaseController;
import cn.qingweico.api.controller.user.PassportControllerApi;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.AppUser;
import cn.qingweico.pojo.bo.PasswordAuthBO;
import cn.qingweico.pojo.bo.RegistLoginBO;
import cn.qingweico.user.handler.DefaultHandler;
import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.CheckUtils;
import cn.qingweico.util.IpUtils;
import cn.qingweico.util.JsonUtils;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author:qiming
 * @date: 2021/9/5
 */
@RestController
public class PassportController extends BaseController implements PassportControllerApi {

    @Resource
    private UserService userService;
    @Resource
    private LoginLogService loginLogService;

    @Override
    public GraceJsonResult getSmsCode(String mobile, HttpServletRequest request) {
        if(!CheckUtils.checkMobileNumber(mobile)) {
            return new GraceJsonResult(ResponseStatusEnum.ILLEGAL_MOBILE_NUMBER_FORMAT);
        }
        // 获取用户的ip
        String userIp = IpUtils.getRequestIp(request);

        // 根据用户的ip进行限制, 限制用户在60s内只能获得一次验证码
        redisOperator.setnx60s("ip:" + userIp, userIp);


        String random = (int) ((Math.random() * 9 + 1) * 100000) + "";

        // 把验证码存入redis中, 用于后续验证
        System.out.println("手机验证码:" + random);
        redisOperator.set(MOBILE_CODE + ":" + mobile, random, 30 * 60);
        return new GraceJsonResult(ResponseStatusEnum.SMS_SEND_SUCCESS);
    }

    @Override
    @SentinelResource(value = "freePwdAuth", blockHandler = "reqFrequentError",
            blockHandlerClass = DefaultHandler.class)
    public GraceJsonResult freePwdAuth(RegistLoginBO registBO,
                                       HttpServletRequest req,
                                       HttpServletResponse resp) {

        String mobile = registBO.getMobile();
        String smsCode = registBO.getSmsCode();
        // 校验验证码是否匹配
        String redisSmsCode = redisOperator.get(MOBILE_CODE + ":" + mobile);
        if (StringUtils.isBlank(redisSmsCode) || !redisSmsCode.equalsIgnoreCase(smsCode)) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 查询数据库, 判断用户是否注册
        AppUser user = userService.queryMobileIsExist(mobile);
        if (user != null && UserStatus.FROZEN.type.equals(user.getActiveStatus())) {
            // 如果用户不为空且状态为冻结则禁止该用户登陆
            return GraceJsonResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        } else if (user == null) {
            // 如果用户没有注册过 则为null 需要注册信息入库
            user = userService.createUser(mobile);
        }
        saveUserAuthToken(user, req, resp);
        int userStatus = user.getActiveStatus();
        // 用户登录或者注册成功后, 需要删除redis中的短信验证码, 验证码只能在使用一次
        redisOperator.del(MOBILE_CODE + ":" + mobile);
        if (userStatus == UserStatus.INACTIVE.type) {
            return new GraceJsonResult(ResponseStatusEnum.WELCOME, user.getActiveStatus());
        } else if (userStatus == UserStatus.ACTIVE.type) {
            return new GraceJsonResult(ResponseStatusEnum.LOGIN_SUCCESS, user.getActiveStatus());
        }
        return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
    }


    @Override
    @SentinelResource(value = "withPwdAuth", blockHandler = "reqFrequentError",
            blockHandlerClass = DefaultHandler.class)
    public GraceJsonResult withPwdAuth(PasswordAuthBO passwordAuthBO,
                                       HttpServletRequest req,
                                       HttpServletResponse resp) {

        String auth = passwordAuthBO.getAuth();
        String password = passwordAuthBO.getPassword();
        AppUser user = userService.queryUserByAuth(auth);
        if (user != null && UserStatus.FROZEN.type.equals(user.getActiveStatus())) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        } else if (user == null) {
            return new GraceJsonResult(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }
        int userStatus = user.getActiveStatus();
        if (user.getMobile().equals(auth)
                || user.getNickname().equals(auth)
                || user.getEmail().equals(auth)) {
            if (user.getPassword().equals(password)) {
                if (userStatus == UserStatus.INACTIVE.type) {
                    return new GraceJsonResult(ResponseStatusEnum.WELCOME, user.getActiveStatus());
                }
                if (userStatus == UserStatus.ACTIVE.type) {
                    saveUserAuthToken(user, req, resp);
                    return new GraceJsonResult(ResponseStatusEnum.LOGIN_SUCCESS, user.getActiveStatus());
                }
            }
        } else {
            return new GraceJsonResult(ResponseStatusEnum.AUTH_FAIL);
        }
        return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
    }

    @Override
    public GraceJsonResult logout(String userId,
                                  HttpServletRequest req,
                                  HttpServletResponse resp) {
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        setCookie(req, resp, "user_token", "", COOKIE_DELETE);
        setCookie(req, resp, "user_id", "", COOKIE_DELETE);
        return GraceJsonResult.ok();
    }

    public void saveUserAuthToken(AppUser user,
                                  HttpServletRequest req,
                                  HttpServletResponse resp) {
        // 保存token以及userInfo到redis中
        String uToken = UUID.randomUUID().toString();
        redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);
        redisOperator.set(REDIS_USER_INFO + ":" + user.getId(), JsonUtils.objectToJson(user));
        // 保存用户id和token到cookie中
        setCookie(req, resp, "user_token", uToken, COOKIE_EXPIRE);
        setCookie(req, resp, "user_id", user.getId(), COOKIE_EXPIRE);
        // 保存用户登陆日志信息
        loginLogService.saveUserLoginLog(user.getId());
    }

}
