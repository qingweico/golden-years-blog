package cn.qingweico.admin.service.impl;

import cn.qingweico.exception.user.UserNotExistsException;
import cn.qingweico.util.StringUtils;

/**
 * 登录校验方法
 *
 * @author zqw
 * @date 2023/4/4
 */
public class SysLoginService {

    public void loginPreCheck(String username, String password) {
        // 用户名或密码为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {

            throw new UserNotExistsException();
        }
        // 校验密码是否在指定范围内

        // 校验用户名是否在指定范围内

        // IP黑名单校验
    }
}
