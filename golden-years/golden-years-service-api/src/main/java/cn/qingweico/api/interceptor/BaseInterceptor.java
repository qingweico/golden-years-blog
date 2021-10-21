package cn.qingweico.api.interceptor;

import cn.qingweico.exception.GraceException;
import cn.qingweico.grace.result.ResponseStatusEnum;
import cn.qingweico.util.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author:qiming
 * @date: 2021/9/7
 */
public class BaseInterceptor {

    @Autowired
    public RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_INFO = "redis_user_info";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";
    public static final String REDIS_ARTICLE_ALREADY_READ = "redis_article_already_read";

    public boolean verifyUserIdAndToken(String id,
                                        String token,
                                        String redisKeyPrefix) {
        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(token)) {
            String redisToken = redisOperator.get(redisKeyPrefix + ":" + id);
            if (!redisToken.equals(token)) {
                GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                return false;
            }
        } else {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }
        return true;
    }
}
