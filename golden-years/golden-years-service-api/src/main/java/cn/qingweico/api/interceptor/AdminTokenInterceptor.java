package cn.qingweico.api.interceptor;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author:qiming
 * @date: 2021/9/9
 */
public class AdminTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {

    /**
     * 拦截请求, 在访问controller调用之前
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  Object
     * @return true or false
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        String adminUserId = request.getHeader("adminUserId");
        String adminUserToken = request.getHeader("adminUserToken");

        return verifyUserIdAndToken(adminUserId, adminUserToken, REDIS_ADMIN_TOKEN);
    }
}