package cn.qingweico.api.base;

import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConf;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.JwtUtils;
import cn.qingweico.util.RedisOperator;
import cn.qingweico.util.ServletReqUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import javax.annotation.Resource;

/**
 * @author zqw
 * @date 2021/9/5
 */
@Slf4j
@EnableFeignClients
public class BaseRestApi {

    @Autowired
    public RedisOperator redisOperator;

    @Resource
    public ElasticsearchTemplate elasticsearchTemplate;

    public Integer getCountsFromRedis(String key) {
        String counts = redisOperator.get(key);
        if (StringUtils.isBlank(counts)) {
            counts = "0";
        }
        return Integer.valueOf(counts);
    }

    public void checkPagingParams(Integer page, Integer pageSize) {
        if (page == null || page <= 0) {
            log.error("page: {}", page);
            GraceException.error(ResponseStatusEnum.FAILED);
        }

        if (pageSize == null || pageSize <= 0) {
            log.error("pageSize: {}", pageSize);
            GraceException.error(ResponseStatusEnum.FAILED);
        }
    }

    public <T> T getLoginUser(Class<T> loginUserType, String tokenKey, String infoKey) {
        String token = ServletReqUtils.getRequest().getHeader("Authorization");
        // 验证JWT
        String id = SysConf.EMPTY_STRING;
        T t = null;
        try {
            Claims claims = JwtUtils.parseJwt(token);
            if (claims != null) {
                id = claims.get(SysConf.USER_ID, String.class);
                t = JsonUtils.jsonToPojo(redisOperator.get(infoKey + SysConf.SYMBOL_COLON + id), loginUserType);
            }
        } catch (Exception e) {
            GraceException.error(ResponseStatusEnum.TICKET_INVALID);
        }
        if (!StringUtils.isEmpty(id)) {
            String redisToken = redisOperator.get(tokenKey + SysConf.SYMBOL_COLON + id);
            if (StringUtils.isEmpty(redisToken) || !token.equals(redisToken)) {
                GraceException.error(ResponseStatusEnum.TICKET_INVALID);
            }
        } else {
            GraceException.error(ResponseStatusEnum.TICKET_INVALID);
        }
        return t;
    }
}
