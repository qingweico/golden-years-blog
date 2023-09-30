package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.service.SysUserService;
import cn.qingweico.core.security.AuthService;
import cn.qingweico.entity.SysUser;
import cn.qingweico.entity.model.LoginUser;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * 用户验证处理
 *
 * @author zqw
 * @data 2023-09-24
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserService userService;

    @Resource
    private SysPasswordService passwordService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.querySysUserByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            log.info("登录用户 : {} 不存在.", username);
            throw new ServiceException("登录用户 : " + username + " 不存在");
        }
        LoginUser loginUser = LoginUser.builder()
                .user(user)
                .build();
        passwordService.validate(loginUser);
        return loginUser;
    }
}
