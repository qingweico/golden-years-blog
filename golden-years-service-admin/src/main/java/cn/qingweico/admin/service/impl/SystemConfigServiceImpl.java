package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.SysConfigMapper;
import cn.qingweico.admin.service.SystemConfigService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.pojo.SysConfig;
import cn.qingweico.result.Response;
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
    private SysConfigMapper sysConfigMapper;

    @Override
    public SysConfig getSystemConfig() {
        String systemConfigRedisKey = RedisConst.REDIS_SYSTEM_CONFIG;
        SysConfig sysConfig;
        final String cachedSystemConfig = redisTemplate.get(systemConfigRedisKey);
        if (StringUtils.isBlank(cachedSystemConfig)) {
            sysConfig = sysConfigMapper.selectAll().get(0);
            redisTemplate.set(systemConfigRedisKey, JsonUtils.objectToJson(sysConfig));
            log.info("set system config cache");
        } else {
            sysConfig = JsonUtils.jsonToPojo(cachedSystemConfig, SysConfig.class);
        }
        return sysConfig;
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
            if (RedisConst.ALL.equals(item)) {
                keys = redisTemplate.keys(SysConst.SYMBOL_STAR);
            } else {
                // 获取Redis中特定前缀
                keys = redisTemplate.keys(item + SysConst.SYMBOL_STAR);
            }
            redisTemplate.delete(keys);
        });
    }

    @Override
    public void alterSystemConfig(SysConfig sysConfig) {
        sysConfig.setUpdateTime(new Date());
        if (sysConfigMapper.updateByPrimaryKeySelective(sysConfig) > 0) {
            String key = RedisConst.REDIS_SYSTEM_CONFIG;
            refreshCache(key);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }
}
