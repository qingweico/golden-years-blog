package cn.qingweico.config;

import cn.qingweico.util.IpUtils;
import cn.qingweico.util.RedisOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * redis限流
 * @author zqw
 * @date 2021/10/15
 */
@Component
public class FilterConfig implements GlobalFilter, Ordered {

    @Resource
    private RedisOperator redisOperator;

    @Value("${blackIp.continueCounts}")
    private Integer continueCounts;
    @Value("${blackIp.timeInterval}")
    private Integer timeInterval;
    @Value("${blackIp.limitTimes}")
    private Integer limitTimes;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest req = exchange.getRequest();
        String visitIp = IpUtils.getRequestIpGateWay(req);
        final String visitIpKey = "visit-ip:" + visitIp;
        final String forbidIp = "forbid-ip:" + visitIp;

        long limitLeftTime = redisOperator.ttl(forbidIp);
        if (limitLeftTime > 0) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
            return exchange.getResponse().setComplete();
        }
        long requestsCounts = redisOperator.increment(visitIpKey, 1);
        if (requestsCounts == 1) {
            redisOperator.expire(visitIpKey, timeInterval);
        }
        if (requestsCounts > continueCounts) {
            redisOperator.set(forbidIp, forbidIp, limitTimes);
            exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

