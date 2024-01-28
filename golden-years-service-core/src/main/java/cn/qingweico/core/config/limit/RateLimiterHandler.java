package cn.qingweico.core.config.limit;

import cn.qingweico.util.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpHeaders;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

/**
 * @author zqw
 * @date 2023/9/30
 */
@Component
@Slf4j
public class RateLimiterHandler {
    private final RedisCache redisCache;

    public RateLimiterHandler(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    private static String script;


    public boolean limit(String name, String key, int limitPeriod, int limitCount) {
        try {
            List<String> keys = new ArrayList<>();
            keys.add(StringUtils.join(key));
            String luaScript = getLuaScript();
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
            Long count = redisCache.execute(redisScript, keys, String.valueOf(limitCount), String.valueOf(limitPeriod));
            log.info("Try access {} count is {} and key = {}", count, name, key);
            if (count != null && count <= limitCount) {
                return true;
            } else {
                throw HttpClientErrorException.create(TOO_MANY_REQUESTS, "Too many request, retry later",
                        HttpHeaders.EMPTY, new byte[0], Charset.defaultCharset());
            }
        } catch (Throwable e) {
            handleException(e, "RateLimiterHandler.limit: {}");
            return false;
        }
    }

    private static String getLuaScript() throws IOException {
        if (StringUtils.isEmpty(script)) {
            script = new ResourceScriptSource(new ClassPathResource("lua/" + "rate_limit.lua")).getScriptAsString();
        }
        return script;
    }

    public void handleException(Throwable e, String msg) {
        log.error(msg, e);
        if (e instanceof HttpClientErrorException) {
            throw HttpClientErrorException.create(((HttpClientErrorException) e).getStatusCode(), ((HttpClientErrorException) e).getStatusText(),
                    HttpHeaders.EMPTY, new byte[0], Charset.defaultCharset());
        } else if (e instanceof RuntimeException) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
        throw new RuntimeException(e.getMessage());

    }
}
