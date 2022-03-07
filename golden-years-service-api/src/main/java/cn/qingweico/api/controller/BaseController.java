package cn.qingweico.api.controller;

import cn.qingweico.global.Constants;
import cn.qingweico.util.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.Inet4Address;

/**
 * @author zqw
 * @date 2021/9/5
 */
public class BaseController {

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
            page = Constants.COMMON_START_PAGE;
        }

        if (pageSize == null || pageSize <= 0) {
            pageSize = Constants.COMMON_PAGE_SIZE;
        }
    }
}
