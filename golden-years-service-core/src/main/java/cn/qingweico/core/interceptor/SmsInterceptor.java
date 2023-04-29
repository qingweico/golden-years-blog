package cn.qingweico.core.interceptor;

import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.util.IpUtils;
import cn.qingweico.util.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zqw
 * @date 2021/9/6
 */
public class SmsInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisCache redisOperator;

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request,
                             @Nonnull HttpServletResponse response,
                             @Nonnull Object handler) {
        // 获取用户的ip
        String userIp = IpUtils.getRequestIp(request);
        boolean keyIsPresent = redisOperator.keyIsExist(RedisConst.REDIS_IP + SysConst.SYMBOL_COLON + userIp);
        if (keyIsPresent) {
            GraceException.error(Response.SMS_NEED_WAIT_ERROR);
            return false;
        }
        return true;
    }
}
