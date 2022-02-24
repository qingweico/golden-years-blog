package cn.qingweico.api.controller.user;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.PasswordAuthBO;
import cn.qingweico.pojo.bo.RegistLoginBO;
import cn.qingweico.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author zqw
 * @date 2021/9/5
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
    @ApiOperation(value = "手机号码登陆", notes = "手机号码登陆", httpMethod = "POST")
    @PostMapping("/mobile")
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
    @PostMapping("/passwd")
    GraceJsonResult withPwdAuth(@RequestBody @Valid PasswordAuthBO passwordAuthBO,
                            HttpServletRequest req,
                            HttpServletResponse resp);


    /**
     * 验证jwt
     * @param token jsonWebToken
     * @return GraceJsonResult
     */
    @ApiOperation(value = "验证jwt", notes = "验证jwt", httpMethod = "GET")
    @GetMapping("/verify/{token}")
    GraceJsonResult authVerify(@PathVariable ("token")String token);

    /**
     * 删除用户token
     * @param token jsonWebToken
     * @return GraceJsonResult
     */
    @ApiOperation(value = "删除用户token", notes = "删除用户token", httpMethod = "GET")
    @GetMapping("/delete/{token}")
    GraceJsonResult deleteUserAccessToken(@PathVariable ("token")String token);
}
