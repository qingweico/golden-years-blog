package cn.qingweico.core.interceptor;

import cn.qingweico.entity.SysUser;
import cn.qingweico.entity.User;
import cn.qingweico.global.RedisConst;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zqw
 * @date 2021/9/6
 */
public class UserTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @Nonnull HttpServletResponse response,
                             @Nonnull Object handler) {
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }
        // 如果是管理员则允许操作
        String tokenKey = RedisConst.REDIS_ADMIN_TOKEN;
        String infoKey = RedisConst.REDIS_ADMIN_INFO;

        String userTokenKey = RedisConst.REDIS_USER_TOKEN;
        String userInfoKey = RedisConst.REDIS_USER_INFO;
        try {
            SysUser sysUser = baseRestApi.getLoginUser(SysUser.class, tokenKey, infoKey);
            if (sysUser != null) {
                return true;
            }
        } catch (Exception e) {
            User user;
            try {
                user = baseRestApi.getLoginUser(User.class, userTokenKey, userInfoKey);
            } catch (Exception ex) {
                return false;
            }
            return user != null;
        }
        return true;
    }
}
