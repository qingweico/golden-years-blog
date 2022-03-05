package cn.qingweico.api.interceptor;

import cn.qingweico.global.RedisConf;
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
        String token = request.getHeader("Authorization");
        return verifyUserToken(token, RedisConf.REDIS_USER_TOKEN);
    }
}
