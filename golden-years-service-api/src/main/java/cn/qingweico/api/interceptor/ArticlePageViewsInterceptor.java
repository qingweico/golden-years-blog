package cn.qingweico.api.interceptor;

import cn.qingweico.global.RedisConf;
import cn.qingweico.global.SysConf;
import cn.qingweico.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zqw
 * @date 2022/5/14
 */
@Slf4j
public class ArticlePageViewsInterceptor extends BaseInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request,
                             @Nonnull HttpServletResponse response,
                             @Nonnull Object handler) {

        String articleId = request.getParameter(SysConf.ARTICLE_ID);
        String visitIp = IpUtils.getRequestIp(request);
        boolean isPresent = redisOperator.keyIsExist(RedisConf.REDIS_ARTICLE_ALREADY_READ + SysConf.SYMBOL_COLON + articleId + SysConf.SYMBOL_COLON + visitIp);
        return !isPresent;
    }
}
