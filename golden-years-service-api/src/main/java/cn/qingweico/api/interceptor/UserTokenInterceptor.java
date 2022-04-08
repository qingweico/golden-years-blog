package cn.qingweico.api.interceptor;

import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Admin;
import cn.qingweico.pojo.User;
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
        String origin = request.getHeader("Origin");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "1800");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,token,Origin,Content-Type,Accept");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }
        // 如果是管理员则允许操作
        String tokenKey = RedisConf.REDIS_ADMIN_TOKEN;
        String infoKey = RedisConf.REDIS_ADMIN_INFO;

        String userTokenKey = RedisConf.REDIS_USER_TOKEN;
        String userInfoKey = RedisConf.REDIS_USER_INFO;
        try {
            Admin admin = baseRestApi.getLoginUser(Admin.class, tokenKey, infoKey);
            if (admin != null) {
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
