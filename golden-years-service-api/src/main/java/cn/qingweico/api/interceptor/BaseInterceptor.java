package cn.qingweico.api.interceptor;

import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.util.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zqw
 * @date 2021/9/7
 */
public class BaseInterceptor {

    @Autowired
    public RedisTemplate redisOperator;

    public BaseRestApi baseRestApi = new BaseRestApi();

}
