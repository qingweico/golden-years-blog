package cn.qingweico.api.interceptor;

import cn.qingweico.exception.GraceException;
import cn.qingweico.global.Constants;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zqw
 * @date 2021/9/7
 */
public class BaseInterceptor {

    @Autowired
    public RedisOperator redisOperator;


    public boolean verifyUserIdAndToken(String id,
                                        String token,
                                        String redisKeyPrefix) {
        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(token)) {
            String redisToken = redisOperator.get(redisKeyPrefix + Constants.SYMBOL_COLON + id);
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
