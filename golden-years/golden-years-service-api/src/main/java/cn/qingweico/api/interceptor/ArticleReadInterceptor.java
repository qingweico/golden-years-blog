package cn.qingweico.api.interceptor;

import cn.qingweico.util.IpUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author:qiming
 * @date: 2021/9/13
 */
public class ArticleReadInterceptor extends BaseInterceptor implements HandlerInterceptor {

    /**
     * 拦截请求, 在访问controller调用之前
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  Object
     * @return true or false
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        String articleId = request.getParameter("articleId");
        String visitIP = IpUtils.getRequestIp(request);
        return !redisOperator.keyIsExist(REDIS_ARTICLE_ALREADY_READ + ":"  + articleId + ":" + visitIP);
    }

}