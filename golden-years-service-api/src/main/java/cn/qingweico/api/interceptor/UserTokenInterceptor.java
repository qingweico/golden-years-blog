package cn.qingweico.api.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
 * @author:qiming
 * @date: 2021/9/6
 */
public class UserTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {

   @Override
   public boolean preHandle(@NotNull HttpServletRequest request,
                            @NotNull HttpServletResponse response,
                            @NotNull Object handler) {

      String userId = request.getHeader("headerUserId");
      String token = request.getHeader("headerUserToken");

      return verifyUserIdAndToken(userId, token, REDIS_USER_TOKEN);
   }
}
