package cn.qingweico.api.controller;

import cn.qingweico.util.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author zqw
 * @date 2021/9/5
 */
public class BaseController {

    @Autowired
    public RedisOperator redisOperator;

    @Resource
    public RestTemplate restTemplate;

    @Resource
    public ElasticsearchTemplate elasticsearchTemplate;

    public Integer getCountsFromRedis(String key) {
        String counts = redisOperator.get(key);
        if (StringUtils.isBlank(counts)) {
            counts = "0";
        }
        return Integer.valueOf(counts);
    }
}
