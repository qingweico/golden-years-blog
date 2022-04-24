package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.WebConfigMapper;
import cn.qingweico.admin.service.WebConfigService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.WebConfig;
import cn.qingweico.result.ResponseStatusEnum;
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
        String webConfigRedisKey = RedisConf.REDIS_WEB_CONFIG;
        WebConfig webConfig;
        final String cachedWebConfig = redisOperator.get(webConfigRedisKey);
        if (StringUtils.isBlank(cachedWebConfig)) {
            webConfig = webConfigMapper.selectAll().get(0);
            redisOperator.set(webConfigRedisKey, JsonUtils.objectToJson(webConfig));
            log.info("set webConfig cache");
        } else {
            webConfig = JsonUtils.jsonToPojo(cachedWebConfig, WebConfig.class);
        }
        return webConfig;
    }

    @Override
    public void alterWebConfig(WebConfig webConfig) {
        String loginTypes = webConfig.getLoginTypeList();
        String result = "[" +
                loginTypes +
                "]";
        webConfig.setLoginTypeList(result);
            webConfig.setUpdateTime(new Date());
        if (webConfigMapper.updateByPrimaryKeySelective(webConfig) > 0) {
            String key = RedisConf.REDIS_WEB_CONFIG;
            refreshCache(key);
        } else {
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }
}
