package cn.qingweico.admin.service;


import cn.qingweico.entity.SysConfig;

import java.util.List;

/**
 * @author zqw
 * @date 2022/4/12
 */
public interface SystemConfigService {

    /**
     * 获取系统配置
     *
     * @return SystemConfig
     */
    SysConfig getSystemConfig();

    /**
     * 通过Key前缀清空Redis缓存
     *
     * @param key keys
     */
    void cleanRedisByKey(List<String> key);

    /**
     * 修改系统配置
     * @param sysConfig SystemConfig
     */
    void alterSystemConfig(SysConfig sysConfig);
}
