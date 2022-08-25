package cn.qingweico.api.interceptor;

import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConf;
import cn.qingweico.global.SysConf;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.IpUtils;
import cn.qingweico.util.RedisTemplate;
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
    private RedisTemplate redisOperator;

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request,
                             @Nonnull HttpServletResponse response,
                             @Nonnull Object handler) {
        // 获取用户的ip
        String userIp = IpUtils.getRequestIp(request);
        boolean keyIsPresent = redisOperator.keyIsExist(RedisConf.REDIS_IP + SysConf.SYMBOL_COLON + userIp);
        if (keyIsPresent) {
            GraceException.error(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        return true;
    }
}
