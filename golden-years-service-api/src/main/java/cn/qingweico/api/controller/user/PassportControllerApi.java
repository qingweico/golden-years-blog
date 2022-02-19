package cn.qingweico.api.controller.user;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.PasswordAuthBO;
import cn.qingweico.pojo.bo.RegistLoginBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author:qiming
 * @date: 2021/9/5
 */
@Api(value = "用户注册登陆相关的接口定义", tags = {"用户注册登陆相关的接口定义"})
@RequestMapping("auth")
public interface PassportControllerApi {

    /**
     * 获得短信验证码
     * @param mobile 用户手机号码
     * @param request HttpServletRequest
     * @return GraceJsonResult
     */
    @ApiOperation(value = "获得短信验证码", notes = "获得短信验证码", httpMethod = "GET")
    @GetMapping("/getSmsCode")
    GraceJsonResult getSmsCode(String mobile, HttpServletRequest request);

    /**
     * 一键注册登陆
     * @param registBO 用户注册信息
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     * @return GraceJsonResult
     */
    @ApiOperation(value = "一键注册登陆", notes = "一键注册登陆", httpMethod = "POST")
    @PostMapping("/freePwdAuth")
    GraceJsonResult freePwdAuth(@RequestBody @Valid RegistLoginBO registBO,
                            HttpServletRequest req,
                            HttpServletResponse resp);

    /**
     * 密码认证登陆
     * @param passwordAuthBO 密码认证登陆信息
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     * @return GraceJsonResult
     */
    @ApiOperation(value = "密码认证登陆", notes = "密码认证登陆", httpMethod = "POST")
    @PostMapping("/withPwdAuth")
    GraceJsonResult withPwdAuth(@RequestBody @Valid PasswordAuthBO passwordAuthBO,
                            HttpServletRequest req,
                            HttpServletResponse resp);

    /**
     * 用户退出登陆
     * @param userId 用户id
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     * @return GraceJsonResult
     */
    @ApiOperation(value = "用户退出登陆", notes = "用户退出登陆", httpMethod = "POST")
    @PostMapping("/logout")
    GraceJsonResult logout(String userId,
                           HttpServletRequest req,
                           HttpServletResponse resp);
}
