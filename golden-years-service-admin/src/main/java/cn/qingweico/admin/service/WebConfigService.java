package cn.qingweico.admin.service;

import cn.qingweico.pojo.WebConfig;

/**
 * @author zqw
 * @date 2022/4/11
 */
public interface WebConfigService {
    /**
     * 获取网站配置
     *
     * @return {@link WebConfig}
     */
    WebConfig getWebConfig();

    /**
     * 修改网站配置
     *
     * @param webConfig {@link WebConfig}
     */
    void alterWebConfig(WebConfig webConfig);
}
