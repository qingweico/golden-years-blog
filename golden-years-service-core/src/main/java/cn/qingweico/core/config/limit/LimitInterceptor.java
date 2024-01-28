package cn.qingweico.core.config.limit;

import cn.qingweico.util.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Optional;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

/**
 * @author zqw
 * @date 2023/9/30
 */
@Aspect
@Configuration
@Slf4j
@EnableConfigurationProperties(RateLimiterProperties.class)
@RequiredArgsConstructor
public class LimitInterceptor {
    private final RateLimiterProperties rateLimiterProperties;

    private final RateLimiterHandler rateLimiterHandler;

    @Around("execution(public * *(..)) && @annotation(RateLimiter)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        try {
            if (!rateLimiterProperties.isEnable()) {
                return pjp.proceed();
            }
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            Method method = signature.getMethod();
            RateLimiter limitAnnotation = method.getAnnotation(RateLimiter.class);
            LimitType limitType = limitAnnotation.limitType();
            String name = limitAnnotation.name();
            String key;
            int limitPeriod = limitAnnotation.period();
            int limitCount = limitAnnotation.count();
            switch (limitType) {
                case IP:
                    key = getIpAddress();
                    break;
                case CUSTOMER:
                    key = limitAnnotation.key();
                    break;
                case URI:
                    key = StringUtils.upperCase(getUri());
                    break;
                default:
                    key = StringUtils.upperCase(method.getName());
            }
            if (StringUtils.isEmpty(key)) {
                key = StringUtils.upperCase(method.getName());
            }

            if (rateLimiterHandler.limit(name, StringUtils.join(limitAnnotation.prefix(), key), limitPeriod, limitCount)) {
                return pjp.proceed();
            } else {
                throw HttpClientErrorException.create(TOO_MANY_REQUESTS, "Too many request, retry later",
                        HttpHeaders.EMPTY, new byte[0], Charset.defaultCharset());
            }
        } catch (Throwable e) {
            rateLimiterHandler.handleException(e, "LimitInterceptor.interceptor: {}");
            return false;
        }

    }

    private Optional<HttpServletRequest> getRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == sra) {
            return Optional.empty();
        }

        return Optional.of(sra.getRequest());
    }

    private String getUri() {
        Optional<HttpServletRequest> request = getRequest();
        return request.map(HttpServletRequest::getRequestURI).orElse(null);
    }

    private String getIpAddress() {
        Optional<HttpServletRequest> requestOptional = getRequest();
        if (requestOptional.isPresent()) {
            HttpServletRequest request = requestOptional.get();
            return IpUtils.getRequestIp(request);
        }
        return null;
    }
}
