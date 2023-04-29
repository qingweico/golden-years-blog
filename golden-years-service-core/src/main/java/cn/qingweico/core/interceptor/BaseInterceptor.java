package cn.qingweico.core.interceptor;

import cn.qingweico.core.base.BaseController;
import cn.qingweico.util.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zqw
 * @date 2021/9/7
 */
public class BaseInterceptor {

    @Autowired
    public RedisCache redisOperator;

    public BaseController baseRestApi = new BaseController();

}
