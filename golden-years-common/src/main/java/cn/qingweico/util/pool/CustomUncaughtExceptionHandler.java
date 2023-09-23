package cn.qingweico.util.pool;

import lombok.extern.slf4j.Slf4j;

/**
 * Customizable UncaughtExceptionHandler
 * custom : (定做的, 量身设计的)
 * customized : (定制的; 用户化的)
 * customizable : (可定制的)
 *
 * @author zqw
 * @date 2023/6/27
 */
@Slf4j
public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error(e.getMessage());
    }
}
