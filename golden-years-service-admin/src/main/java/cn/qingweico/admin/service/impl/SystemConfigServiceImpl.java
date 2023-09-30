package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.SysConfigMapper;
import cn.qingweico.admin.service.SystemConfigService;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.entity.SysConfig;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.redis.RedisCache;
import cn.qingweico.util.redis.RedisUtil;
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
    @Resource
    private RedisCache redisCache;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public SysConfig getSystemConfig() {
        String systemConfigRedisKey = RedisConst.REDIS_SYSTEM_CONFIG;
        SysConfig sysConfig;
        final String cachedSystemConfig = redisCache.get(systemConfigRedisKey);
        if (StringUtils.isBlank(cachedSystemConfig)) {
            sysConfig = sysConfigMapper.selectList(null).get(0);
            redisCache.set(systemConfigRedisKey, JsonUtils.objectToJson(sysConfig));
            log.info("set system config cache");
        } else {
            sysConfig = JsonUtils.jsonToPojo(cachedSystemConfig, SysConfig.class);
        }
        return sysConfig;
    }

    @Override
    public void cleanRedisByKey(List<String> key) {
        if (key == null) {
            return;
        }
        key.forEach(item -> {
            // 清空所有key
            Set<String> keys;
            if (RedisConst.ALL.equals(item)) {
                keys = redisCache.keys(SysConst.SYMBOL_STAR);
            } else {
                // 获取Redis中特定前缀
                keys = redisCache.keys(item + SysConst.SYMBOL_STAR);
            }
            redisCache.delete(keys);
        });
    }

    @Override
    public void alterSystemConfig(SysConfig sysConfig) {
        sysConfig.setUpdateTime(DateUtils.nowDateTime());
        if (sysConfigMapper.updateById(sysConfig) > 0) {
            String key = RedisConst.REDIS_SYSTEM_CONFIG;
            redisUtil.clearCache(key);
        }
    }
}
