package cn.qingweico.user.restapi;

import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.global.SysConf;
import cn.qingweico.global.RedisConf;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.User;
import cn.qingweico.pojo.bo.PasswordAuthBO;
import cn.qingweico.pojo.bo.SmsMobileBO;
import cn.qingweico.user.handler.DefaultHandler;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.CheckUtils;
import cn.qingweico.util.IpUtils;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.JwtUtils;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
public class PassportRestApi extends BaseRestApi {

    @Resource
    private UserService userService;

    @ApiOperation(value = "获得短信验证码", notes = "获得短信验证码", httpMethod = "GET")
    @GetMapping("/getSmsCode")
    public GraceJsonResult getSmsCode(@RequestParam String mobile, HttpServletRequest request) {
        if (!CheckUtils.checkMobileNumber(mobile)) {
            return new GraceJsonResult(ResponseStatusEnum.ILLEGAL_MOBILE_NUMBER_FORMAT);
        }
        // 获取用户的ip
        String userIp = IpUtils.getRequestIp(request);

        // 根据用户的ip进行限制, 限制用户在60s内只能获得一次验证码
        redisOperator.setnx60s(RedisConf.REDIS_IP + SysConf.SYMBOL_COLON + userIp, userIp);


        String random = (int) ((Math.random() * 9 + 1) * 100000) + SysConf.EMPTY_STRING;

        // 把验证码存入redis中, 用于后续验证; 验证码两分钟内有效
        redisOperator.set(RedisConf.MOBILE_SMS_CODE + SysConf.SYMBOL_COLON + mobile, random, 2 * 60);
        return new GraceJsonResult(ResponseStatusEnum.SMS_SEND_SUCCESS, random);
    }

    @ApiOperation(value = "手机号码登陆", notes = "手机号码登陆", httpMethod = "POST")
    @PostMapping("/mobile")
    @SentinelResource(value = "freePwdAuth", blockHandler = "reqFrequentError",
            blockHandlerClass = DefaultHandler.class)
    public GraceJsonResult freePwdAuth(@RequestBody SmsMobileBO registerBO) {

        String mobile = registerBO.getMobile();
        String smsCode = registerBO.getSmsCode();
        // 校验手机验证码是否匹配
        String redisSmsCode = redisOperator.get(RedisConf.MOBILE_SMS_CODE + SysConf.SYMBOL_COLON + mobile);
        if (StringUtils.isBlank(redisSmsCode) || !redisSmsCode.equalsIgnoreCase(smsCode)) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 查询数据库, 判断用户是否注册
        User user = userService.queryMobileIsPresent(mobile);
        if (user == null) {
            // 如果用户没有注册过, 则为null, 需要注册信息入库
            user = userService.createUser(mobile);
        } else if (UserStatus.FROZEN.type.equals(user.getActiveStatus())) {
            // 如果用户不为空且状态为冻结则禁止该用户登陆
            return GraceJsonResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        }
        String jsonWebToken = JwtUtils.createJwt(user.getId());
        userService.doSaveUserAuthToken(user, jsonWebToken);
        userService.doSaveLoginLog(user.getId());
        int userStatus = user.getActiveStatus();
        // 用户登录或者注册成功后, 需要删除redis中的短信验证码, 验证码只能在使用一次
        redisOperator.del(RedisConf.MOBILE_SMS_CODE + SysConf.SYMBOL_COLON + mobile);
        HashMap<String, Object> map = new HashMap<>(2);
        map.put(SysConf.TOKEN, jsonWebToken);
        map.put(SysConf.USER_STATUS, user.getActiveStatus());
        if (userStatus == UserStatus.INACTIVE.type) {
            return new GraceJsonResult(ResponseStatusEnum.WELCOME, map);
        } else if (userStatus == UserStatus.ACTIVE.type) {
            return new GraceJsonResult(ResponseStatusEnum.LOGIN_SUCCESS, map);
        }
        return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
    }


    @ApiOperation(value = "密码认证登陆", notes = "密码认证登陆", httpMethod = "POST")
    @PostMapping("/passwd")
    @SentinelResource(value = "withPwdAuth", blockHandler = "reqFrequentError",
            blockHandlerClass = DefaultHandler.class)
    public GraceJsonResult withPwdAuth(@RequestBody PasswordAuthBO passwordAuthBO) {
        String auth = passwordAuthBO.getAuth();
        String password = passwordAuthBO.getPassword();
        User user = userService.queryUserByAuth(auth);
        if (user == null) {
            return new GraceJsonResult(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        } else if (UserStatus.FROZEN.type.equals(user.getActiveStatus())) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        }
        int userStatus = user.getActiveStatus();
        if (user.getMobile().equals(auth)
                || user.getNickname().equals(auth)
                || user.getEmail().equals(auth)) {
            if (Objects.equals(user.getPassword(), password)) {
                if (userStatus == UserStatus.INACTIVE.type) {
                    return new GraceJsonResult(ResponseStatusEnum.WELCOME, user.getActiveStatus());
                }
                if (userStatus == UserStatus.ACTIVE.type) {
                    String jsonWebToken = JwtUtils.createJwt(user.getId());
                    userService.doSaveUserAuthToken(user, jsonWebToken);
                    userService.doSaveLoginLog(user.getId());
                    return new GraceJsonResult(ResponseStatusEnum.LOGIN_SUCCESS, jsonWebToken);
                }
            }
        }
        return new GraceJsonResult(ResponseStatusEnum.AUTH_FAIL);
    }


    @ApiOperation(value = "验证JWT", notes = "验证JWT", httpMethod = "GET")
    @GetMapping("/verify")
    public GraceJsonResult authVerify(@RequestParam String token) {
        String userId = SysConf.EMPTY_STRING;
        User user = null;
        try {
            Claims claims = JwtUtils.parseJwt(token);
            if (claims != null) {
                userId = claims.get(SysConf.USER_ID, String.class);
                user = JsonUtils.jsonToPojo(redisOperator.get(RedisConf.REDIS_USER_INFO + SysConf.SYMBOL_COLON + userId), User.class);
            }
        } catch (Exception e) {
            return new GraceJsonResult(ResponseStatusEnum.TICKET_INVALID);
        }
        if (!StringUtils.isEmpty(userId)) {
            String redisToken = redisOperator.get(RedisConf.REDIS_USER_TOKEN + SysConf.SYMBOL_COLON + userId);
            if (StringUtils.isEmpty(redisToken)) {
                return new GraceJsonResult(ResponseStatusEnum.TICKET_INVALID);
            }
        } else {
            return new GraceJsonResult(ResponseStatusEnum.TICKET_INVALID);
        }
        return GraceJsonResult.ok(user);
    }

    @ApiOperation(value = "删除用户token", notes = "删除用户token", httpMethod = "POST")
    @PostMapping("/delete")
    public GraceJsonResult deleteUserAccessToken(@RequestBody String token) {
        String userId = SysConf.EMPTY_STRING;
        try {
            Claims claims = JwtUtils.parseJwt(token);
            if (claims != null) {
                userId = claims.get(SysConf.USER_ID, String.class);
            }
        } catch (Exception e) {
            return new GraceJsonResult(ResponseStatusEnum.TICKET_INVALID);
        }
        redisOperator.del(RedisConf.REDIS_USER_TOKEN + SysConf.SYMBOL_COLON + userId);
        redisOperator.del(RedisConf.REDIS_USER_INFO + SysConf.SYMBOL_COLON + userId);
        return GraceJsonResult.ok();
    }

    @ApiOperation(value = "检查手机验证码", notes = "检查手机验证码", httpMethod = "POST")
    @PostMapping("/verify/smsCode")
    public GraceJsonResult checkSmsCode(@RequestBody SmsMobileBO smsMobileBO) {
        String smsCode = smsMobileBO.getSmsCode();
        String mobile = smsMobileBO.getMobile();
        if(StringUtils.isBlank(smsCode)) {
            return new GraceJsonResult(ResponseStatusEnum.SMS_CODE_NULL);
        }
        String redisSmsCode = redisOperator.get(RedisConf.MOBILE_SMS_CODE + SysConf.SYMBOL_COLON + mobile);
        if(!Objects.equals(smsCode, redisSmsCode)) {
            return new GraceJsonResult(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        return GraceJsonResult.ok();
    }
}
