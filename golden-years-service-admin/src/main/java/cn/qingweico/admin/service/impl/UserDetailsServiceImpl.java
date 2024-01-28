package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.service.SysPermissionService;
import cn.qingweico.admin.service.SysUserService;
import cn.qingweico.entity.SysUser;
import cn.qingweico.entity.model.LoginUser;
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

    @Resource
    private SysPermissionService permissionService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.querySysUserByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            log.info("登录用户 : {} 不存在", username);
            throw new UsernameNotFoundException("登录用户 : " + username + " 不存在");
        }
        LoginUser loginUser = LoginUser.builder()
                .user(user)
                .userId(user.getId())
                .build();
        passwordService.validate(loginUser);
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {
        return new LoginUser(user.getId(), user, permissionService.getMenuPermission(user));
    }
}
