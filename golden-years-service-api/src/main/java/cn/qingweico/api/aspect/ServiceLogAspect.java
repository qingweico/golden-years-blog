package cn.qingweico.api.aspect;

import cn.qingweico.global.Constants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author:qiming
 * @date: 2021/9/7
 */
@Aspect
public class ServiceLogAspect {
    private static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Around("execution(* cn.qingweico.*.service.impl..*.*(..))")
    public Object aspectService(ProceedingJoinPoint proceedingJoinPoint) {

        log.info("开始执行 -----> {}, {}", proceedingJoinPoint.getTarget().getClass(),
                proceedingJoinPoint.getSignature().getName());

        long start = System.currentTimeMillis();
        Object proceed = null;

        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        long end = System.currentTimeMillis();
        long cost = end - start;
        if (cost > Constants.NUM_1000) {
            log.error("当前执行用时: {}", cost);
        } else if (cost > Constants.NUM_500) {
            log.warn("当前执行用时: {}", cost);
        } else {
            log.info("当前执行用时: {}", cost);
        }
        return proceed;

    }

}
