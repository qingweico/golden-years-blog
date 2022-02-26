package cn.qingweico.api.interceptor;

import cn.qingweico.exception.GraceException;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.IpUtils;
import cn.qingweico.util.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
 * @author zqw
 * @date 2021/9/6
 */
public class PassportInterceptor implements HandlerInterceptor {

   @Autowired
   private RedisOperator redisOperator;

   @Override
   public boolean preHandle(@Nonnull HttpServletRequest request,
                            @Nonnull HttpServletResponse response,
                            @Nonnull Object handler) {
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
