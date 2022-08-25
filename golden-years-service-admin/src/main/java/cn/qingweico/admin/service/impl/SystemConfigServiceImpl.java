package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.SystemConfigMapper;
import cn.qingweico.admin.service.SystemConfigService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConf;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.SystemConfig;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author zqw
 * @date 2022/4/12
 */
@Slf4j
@Service
public class SystemConfigServiceImpl extends BaseService implements SystemConfigService {

    @Resource
    private SystemConfigMapper systemConfigMapper;

    @Override
    public SystemConfig getSystemConfig() {
        String systemConfigRedisKey = RedisConf.REDIS_SYSTEM_CONFIG;
        SystemConfig systemConfig;
        final String cachedSystemConfig = redisTemplate.get(systemConfigRedisKey);
        if (StringUtils.isBlank(cachedSystemConfig)) {
            systemConfig = systemConfigMapper.selectAll().get(0);
            redisTemplate.set(systemConfigRedisKey, JsonUtils.objectToJson(systemConfig));
            log.info("set systemConfig cache");
        } else {
            systemConfig = JsonUtils.jsonToPojo(cachedSystemConfig, SystemConfig.class);
        }
        return systemConfig;
    }

    @Override
    public void cleanRedisByKey(List<String> key) {
        if (key == null) {
            log.error("redis key is null");
            return;
        }
        key.forEach(item -> {
            // 清空所有key
            Set<String> keys;
            if (RedisConf.ALL.equals(item)) {
                keys = redisTemplate.keys(SysConf.SYMBOL_STAR);
            } else {
                // 获取Redis中特定前缀
                keys = redisTemplate.keys(item + SysConf.SYMBOL_STAR);
            }
            redisTemplate.delete(keys);
        });
    }

    @Override
    public void alterSystemConfig(SystemConfig systemConfig) {
        systemConfig.setUpdateTime(new Date());
        if (systemConfigMapper.updateByPrimaryKeySelective(systemConfig) > 0) {
            String key = RedisConf.REDIS_SYSTEM_CONFIG;
            refreshCache(key);
        } else {
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }
}
