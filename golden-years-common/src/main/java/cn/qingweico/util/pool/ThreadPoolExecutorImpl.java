package cn.qingweico.util.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zqw
 * @date 2022/8/19
 */
@Slf4j
public class ThreadPoolExecutorImpl extends ThreadPoolExecutor {
    public ThreadPoolExecutorImpl(int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue,
                                  boolean isEnableMonitor) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.isEnableMonitor = isEnableMonitor;
    }

    long start = 0;
    long thisTimeCost = 0;
    double maxExecutionTime = -1;
    int totalExecutionCount = 0;
    double minExecutionTime = Integer.MAX_VALUE;
    double totalExecutionTime = 0.0;
    boolean isEnableMonitor;

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (isEnableMonitor) {
            start = System.currentTimeMillis();
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (isEnableMonitor) {
            thisTimeCost = System.currentTimeMillis() - start;
            totalExecutionTime += thisTimeCost;
            totalExecutionCount++;
            if (minExecutionTime > thisTimeCost) {
                minExecutionTime = thisTimeCost;
            }
            if (thisTimeCost > maxExecutionTime) {
                maxExecutionTime = thisTimeCost;
            }
        }
    }

    @Override
    protected void terminated() {
        super.terminated();
        if (isEnableMonitor) {
            log.info("线程池中任务的最大执行时间为: {}ms", maxExecutionTime);
            log.info("线程池中任务的最小执行时间为: {}ms", minExecutionTime);
            log.info("线程池中任务的平均执行时间为: {}ms", totalExecutionTime / totalExecutionCount);
        }
    }
}
