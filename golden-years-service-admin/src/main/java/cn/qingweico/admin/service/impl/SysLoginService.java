package cn.qingweico.admin.service.impl;

import cn.qingweico.core.security.context.AuthenticationContextHolder;
import cn.qingweico.core.security.TokenService;
import cn.qingweico.entity.model.LoginUser;
import cn.qingweico.exception.ServiceException;
import cn.qingweico.exception.user.UserNotExistsException;
import cn.qingweico.exception.user.UserPasswordNotMatchException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 登录校验方法
 *
 * @author zqw
 * @date 2023/4/4
 */
@Service
public class SysLoginService {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private TokenService tokenService;


    public void loginPreCheck(String username, String password) {
        // 用户名或密码为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new UserNotExistsException();
        }


        // 校验密码是否在指定范围内

        // 校验用户名是否在指定范围内

        // IP黑名单校验
    }

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param uuid     唯一标识
     * @return token
     */
    public String login(String username, String password, String uuid) {

        // 登录前置校验
        loginPreCheck(username, password);
        // 用户认证
        Authentication authentication;
        try {
            // AuthenticationManager authenticate 进行用户认证
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            // 认证没有通过
            if (e instanceof BadCredentialsException) {
                throw new UserPasswordNotMatchException();
            } else {
                throw new InternalAuthenticationServiceException(e.getMessage());
            }
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        // 认证通过, 生成 jwt 返回
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        return tokenService.createToken(loginUser);
    }
}
