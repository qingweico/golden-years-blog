package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.WebConfigMapper;
import cn.qingweico.admin.service.WebConfigService;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.pojo.WebConfig;
import cn.qingweico.result.Response;
import cn.qingweico.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zqw
 * @date 2022/4/11
 */
@Slf4j
@Service
public class WebConfigServiceImpl extends BaseService implements WebConfigService {

    @Resource
    private WebConfigMapper webConfigMapper;

    @Override
    public WebConfig getWebConfig() {
        String webConfigRedisKey = RedisConst.REDIS_WEB_CONFIG;
        WebConfig webConfig;
        final String cachedWebConfig = redisCache.get(webConfigRedisKey);
        if (StringUtils.isBlank(cachedWebConfig)) {
            webConfig = webConfigMapper.selectAll().get(0);
            redisCache.set(webConfigRedisKey, JsonUtils.objectToJson(webConfig));
            log.info("set web config cache");
        } else {
            webConfig = JsonUtils.jsonToPojo(cachedWebConfig, WebConfig.class);
        }
        return webConfig;
    }

    @Override
    public void alterWebConfig(WebConfig webConfig) {
        String loginTypes = webConfig.getLoginTypeList();
        String result =  SysConst.SYMBOL_LEFT_CENTER_BRACKET +
                loginTypes +
                SysConst.SYMBOL_RIGHT_CENTER_BRACKET;
        webConfig.setLoginTypeList(result);
            webConfig.setUpdateTime(new Date());
        if (webConfigMapper.updateByPrimaryKeySelective(webConfig) > 0) {
            String key = RedisConst.REDIS_WEB_CONFIG;
            refreshCache(key);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }
}
