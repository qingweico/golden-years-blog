package cn.qingweico.api.aspect;

import cn.qingweico.global.SysConst;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zqw
 * @date 2021/9/7
 */
@Aspect
@Component
public class ServiceLogAspect {
    private static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Around("execution(* cn.qingweico.*.service.impl..*.*(..))")
    public Object aspectService(ProceedingJoinPoint proceedingJoinPoint) {

        log.info("开始执行: {}, {}", proceedingJoinPoint.getTarget().getClass(),
                proceedingJoinPoint.getSignature().getName());

        long start = System.currentTimeMillis();
        Object proceed = null;

        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable t) {
            log.error(t.getMessage());
        }

        long end = System.currentTimeMillis();
        long cost = end - start;
        if (cost > SysConst.NUM_1000) {
            log.error("当前执行用时: {}ms", cost);
        } else if (cost > SysConst.NUM_500) {
            log.warn("当前执行用时: {}ms", cost);
        } else {
            log.info("当前执行用时: {}ms", cost);
        }
        return proceed;
    }
}
