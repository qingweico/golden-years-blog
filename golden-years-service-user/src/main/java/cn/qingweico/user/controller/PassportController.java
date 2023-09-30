package cn.qingweico.user.controller;

import cn.qingweico.core.base.BaseController;
import cn.qingweico.entity.User;
import cn.qingweico.entity.model.PasswordAuthBO;
import cn.qingweico.entity.model.SmsMobileBO;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.user.handler.DefaultHandler;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.*;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Objects;

/**
 * 主页用户登陆接口
 *
 * @author zqw
 * @date 2021/9/5
 */
@Slf4j
@RestController
@Api(value = "用户注册登陆相关的接口定义", tags = {"用户注册登陆相关的接口定义"})
@RequestMapping("/u/auth")
public class PassportController extends BaseController {

    @Resource
    private UserService userService;
    @Resource
    private SmsUtil smsUtil;

    @ApiOperation(value = "获得短信验证码", notes = "获得短信验证码", httpMethod = "GET")
    @GetMapping("/getSmsCode")
    public Result getSmsCode(@RequestParam String mobile, HttpServletRequest request) {
        if (!CheckUtils.checkMobileNumber(mobile)) {
            return Result.r(Response.ILLEGAL_MOBILE_NUMBER_FORMAT);
        }
        // 获取用户的ip
        String userIp = IpUtils.getRequestIp(request);
        // 根据用户的ip进行限制, 限制用户在60s内只能获得一次验证码
        redisCache.setNx60s(RedisConst.REDIS_IP + SysConst.SYMBOL_COLON + userIp, userIp);
        String random = RandomStringUtils.random(6,false,true);
        smsUtil.sendSms(mobile, random);
        // 把验证码存入redis中, 用于后续验证; 验证码两分钟内有效
        redisCache.set(RedisConst.MOBILE_SMS_CODE + SysConst.SYMBOL_COLON + mobile, random, 2 * 60);
        return Result.r(Response.SMS_SEND_SUCCESS);
    }

    @ApiOperation(value = "手机号码登陆", notes = "手机号码登陆", httpMethod = "POST")
    @PostMapping("/mobile")
    @SentinelResource(value = "freePwdAuth", blockHandler = "reqFrequentError",
            blockHandlerClass = DefaultHandler.class)
    public Result freePwdAuth(@RequestBody SmsMobileBO registerBO) {

        String mobile = registerBO.getMobile();
        String smsCode = registerBO.getSmsCode();
        // 校验手机验证码是否匹配
        String redisSmsCode = redisCache.get(RedisConst.MOBILE_SMS_CODE + SysConst.SYMBOL_COLON + mobile);
        if (StringUtils.isBlank(redisSmsCode) || !redisSmsCode.equalsIgnoreCase(smsCode)) {
            return Result.r(Response.SMS_CODE_ERROR);
        }

        // 查询数据库, 判断用户是否注册
        User user = userService.queryUserByMobile(mobile);
        if (user == null) {
            // 如果用户没有注册过, 则为null, 需要注册信息入库
            user = userService.createUser(mobile);
        } else if (UserStatus.DISABLE.getVal().equals(user.getAvailable())) {
            // 如果用户不为空且状态为冻结则禁止该用户登陆
            return Result.r(Response.USER_FROZEN);
        }
        String jsonWebToken = JwtUtils.createJwt(user.getId());
        userService.doSaveUserAuthToken(user, jsonWebToken);
        userService.doSaveLoginLog(user.getId());
        int userStatus = user.getAvailable();
        // 用户登录或者注册成功后, 需要删除redis中的短信验证码, 验证码只能在使用一次
        redisCache.del(RedisConst.MOBILE_SMS_CODE + SysConst.SYMBOL_COLON + mobile);
        HashMap<String, Object> map = new HashMap<>(2);
        map.put(SysConst.TOKEN, jsonWebToken);
        map.put(SysConst.USER_STATUS, user.getAvailable());
        if (userStatus == UserStatus.DISABLE.getVal()) {
            return Result.ok(Response.WELCOME, map);
        } else if (userStatus == UserStatus.AVAILABLE.getVal()) {
            return Result.ok(Response.LOGIN_SUCCESS, map);
        }
        return Result.r(Response.SYSTEM_ERROR);
    }


    @ApiOperation(value = "密码认证登陆", notes = "密码认证登陆", httpMethod = "POST")
    @PostMapping("/passwd")
    @SentinelResource(value = "withPwdAuth", blockHandler = "reqFrequentError",
            blockHandlerClass = DefaultHandler.class)
    public Result withPwdAuth(@RequestBody PasswordAuthBO passwordAuthBO) {
        String auth = passwordAuthBO.getAuth();
        String password = passwordAuthBO.getPassword();
        User user = userService.queryUserByAuth(auth);
        if (user == null) {
            return Result.r(Response.USER_NOT_EXIST_ERROR);
        } else if (UserStatus.DISABLE.getVal().equals(user.getAvailable())) {
            return Result.r(Response.USER_FROZEN);
        }
        int userStatus = user.getAvailable();
        if (user.getMobile().equals(auth)
                || user.getNickname().equals(auth)
                || user.getEmail().equals(auth)) {
            if (Objects.equals(user.getPassword(), password)) {
                if (userStatus == UserStatus.DISABLE.getVal()) {
                    return Result.ok(Response.WELCOME, user.getAvailable());
                }
                if (userStatus == UserStatus.AVAILABLE.getVal()) {
                    String jsonWebToken = JwtUtils.createJwt(user.getId());
                    userService.doSaveUserAuthToken(user, jsonWebToken);
                    userService.doSaveLoginLog(user.getId());
                    return Result.ok(Response.LOGIN_SUCCESS, jsonWebToken);
                }
            }
        }
        return Result.r(Response.AUTH_FAIL);
    }


    @ApiOperation(value = "验证JWT", notes = "验证JWT", httpMethod = "GET")
    @GetMapping("/verify")
    public Result authVerify(@RequestParam String token) {
        String userId = SysConst.EMPTY_STRING;
        User user = null;
        try {
            Claims claims = JwtUtils.parseJwt(token);
            if (claims != null) {
                userId = claims.get(SysConst.USER_ID, String.class);
                user = JsonUtils.jsonToPojo(redisCache.get(RedisConst.REDIS_USER_INFO + SysConst.SYMBOL_COLON + userId), User.class);
            }
        } catch (Exception e) {
            return Result.r(Response.TICKET_INVALID);
        }
        if (!StringUtils.isEmpty(userId)) {
            String redisToken = redisCache.get(RedisConst.REDIS_USER_TOKEN + SysConst.SYMBOL_COLON + userId);
            if (StringUtils.isEmpty(redisToken)) {
                return Result.r(Response.TICKET_INVALID);
            }
        } else {
            return Result.r(Response.TICKET_INVALID);
        }
        return Result.ok(user);
    }

    @ApiOperation(value = "删除用户token", notes = "删除用户token", httpMethod = "POST")
    @PostMapping("/delete")
    public Result deleteUserAccessToken(@RequestBody String token) {
        String userId = SysConst.EMPTY_STRING;
        try {
            Claims claims = JwtUtils.parseJwt(token);
            if (claims != null) {
                userId = claims.get(SysConst.USER_ID, String.class);
            }
        } catch (Exception e) {
            return Result.r(Response.TICKET_INVALID);
        }
        redisCache.del(RedisConst.REDIS_USER_TOKEN + SysConst.SYMBOL_COLON + userId);
        redisCache.del(RedisConst.REDIS_USER_INFO + SysConst.SYMBOL_COLON + userId);
        return Result.ok();
    }

    @ApiOperation(value = "检查手机验证码", notes = "检查手机验证码", httpMethod = "POST")
    @PostMapping("/verify/smsCode")
    public Result checkSmsCode(@RequestBody SmsMobileBO smsMobileBO) {
        String smsCode = smsMobileBO.getSmsCode();
        String mobile = smsMobileBO.getMobile();
        if (StringUtils.isBlank(smsCode)) {
            return Result.r(Response.SMS_CODE_NULL);
        }
        String redisSmsCode = redisCache.get(RedisConst.MOBILE_SMS_CODE + SysConst.SYMBOL_COLON + mobile);
        if (!Objects.equals(smsCode, redisSmsCode)) {
            return Result.r(Response.SMS_CODE_ERROR);
        }
        return Result.ok();
    }
}
