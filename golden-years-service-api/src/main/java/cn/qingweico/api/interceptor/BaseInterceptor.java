package cn.qingweico.api.interceptor;

import cn.qingweico.exception.GraceException;
import cn.qingweico.global.Constants;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.AppUser;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.JwtUtils;
import cn.qingweico.util.RedisOperator;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zqw
 * @date 2021/9/7
 */
public class BaseInterceptor {

    @Autowired
    public RedisOperator redisOperator;


    public boolean verifyUserToken(String token,
                                   String redisKeyPrefix) {
        if (StringUtils.isNotBlank(token)) {
            String userId = "";
            try {
                Claims claims = JwtUtils.parseJwt(token);
                if (claims != null) {
                    userId = claims.get("user_id", String.class);
                }
            } catch (Exception e) {
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            }

            String redisToken = redisOperator.get(redisKeyPrefix + Constants.SYMBOL_COLON + userId);
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
