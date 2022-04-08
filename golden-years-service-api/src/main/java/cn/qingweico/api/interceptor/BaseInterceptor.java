package cn.qingweico.api.interceptor;

import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.util.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zqw
 * @date 2021/9/7
 */
public class BaseInterceptor {

    @Autowired
    public RedisOperator redisOperator;

    public BaseRestApi baseRestApi = new BaseRestApi();

}
