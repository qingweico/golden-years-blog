package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.WebConfigMapper;
import cn.qingweico.admin.service.WebConfigService;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.entity.WebConfig;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.result.Response;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.redis.RedisCache;
import cn.qingweico.util.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zqw
 * @date 2022/4/11
 */
@Slf4j
@Service
public class WebConfigServiceImpl extends BaseService implements WebConfigService {

    @Resource
    private WebConfigMapper webConfigMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public WebConfig getWebConfig() {
        String webConfigRedisKey = RedisConst.REDIS_WEB_CONFIG;
        WebConfig webConfig;
        final String cachedWebConfig = redisCache.get(webConfigRedisKey);
        if (StringUtils.isEmpty(cachedWebConfig)) {
            webConfig = webConfigMapper.selectList(null).get(0);
            redisCache.set(webConfigRedisKey, JsonUtils.objectToJson(webConfig));
        } else {
            webConfig = JsonUtils.jsonToPojo(cachedWebConfig, WebConfig.class);
        }
        return webConfig;
    }

    @Override
    public void alterWebConfig(WebConfig webConfig) {
        webConfig.setUpdateTime(DateUtils.nowDateTime());
        if (webConfigMapper.updateById(webConfig) > 0) {
            String key = RedisConst.REDIS_WEB_CONFIG;
            redisUtil.clearCache(key);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }
}
