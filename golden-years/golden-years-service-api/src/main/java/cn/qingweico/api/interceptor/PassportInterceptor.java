package cn.qingweico.api.interceptor;

import cn.qingweico.exception.GraceException;
import cn.qingweico.grace.result.ResponseStatusEnum;
import cn.qingweico.util.IpUtils;
import cn.qingweico.util.RedisOperator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author:qiming
 * @date: 2021/9/6
 */
public class PassportInterceptor implements HandlerInterceptor {

   @Autowired
   private RedisOperator redisOperator;

   @Override
   public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
      // 获取用户的ip
      String userIp = IpUtils.getRequestIp(request);
      boolean keyIsExist = redisOperator.keyIsExist("ip:" + userIp);
      if(keyIsExist) {
         GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
         return false;
      }
      return true;
   }
}
