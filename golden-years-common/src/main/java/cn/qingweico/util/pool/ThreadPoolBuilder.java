package cn.qingweico.util.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 使用构建器构建线程池
 *
 * @author zqw
 * @date 2022/8/19
 */
@Slf4j
public class ThreadPoolBuilder {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(int blockQueueSize) {
        return new Builder(blockQueueSize);
    }

    /*single*/

    public static ExecutorService single() {
        return single(false);
    }
    public static ExecutorService single(boolean daemon) {
        return builder(1)
                .corePoolSize(1)
                .maxPoolSize(1)
                .keepAliveTime(1)
                .timeUnit(TimeUnit.SECONDS)
                .allowCoreThreadTimeOut(true)
                .threadFactory(CustomizableThreadFactory.guavaThreadFactory(daemon))
                .build();
    }

    public static class Builder {
        /*default core pool size*/
        private int corePoolSize = 10;
        /*default max pool size*/
        private int maxPoolSize = 10;
        /*default keepAliveTime*/
        private long keepAliveTime = 60L;
        /*default block queue size*/
        private int blockQueueSize = 100;
        private boolean preStartAllCore = false;
        private boolean isEnableMonitor = false;
        private boolean allowCoreThreadTimeOut = false;
        private TimeUnit unit = TimeUnit.MILLISECONDS;
        private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(blockQueueSize);
        private ThreadFactory threadFactory = CustomizableThreadFactory.basicThreadFactory();

        public Builder() {
        }

        /*required*/
        public Builder(int blockQueueSize) {
            this.blockQueueSize = blockQueueSize;
            this.workQueue = new LinkedBlockingQueue<>(blockQueueSize);
        }

        /*optional*/
        public Builder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        /*optional*/
        public Builder maxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        /*optional*/
        public Builder keepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        /*optional*/
        public Builder timeUnit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }

        /*optional*/
        public Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        /*optional*/
        public Builder preStartAllCore(boolean preStartAllCore) {
            this.preStartAllCore = preStartAllCore;
            return this;
        }

        /*optional*/
        public Builder isEnableMonitor(boolean isEnableMonitor) {
            this.isEnableMonitor = isEnableMonitor;
            return this;
        }

        /*optional*/
        public Builder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
            return this;
        }

        public ExecutorService build() {
            ThreadPoolExecutor executor = new ThreadPoolExecutorImpl(this.corePoolSize,
                    this.maxPoolSize, this.keepAliveTime, this.unit, this.workQueue, this.isEnableMonitor);
            if (this.allowCoreThreadTimeOut) {
                log.info("Thread Pool allow core thread time out");
                executor.allowCoreThreadTimeOut(true);
            }
            if (this.preStartAllCore) {
                int coreThreads = executor.prestartAllCoreThreads();
                log.info("{} Core Thread are all started", coreThreads);
            }
            if (this.isEnableMonitor) {
                log.info("Thread Pool Monitor has enable");
                log.info("Core Pool Size: {}", corePoolSize);
                log.info("Max Pool Size: {}", maxPoolSize);
                log.info("Block Queue Size: {}", blockQueueSize);
            }
            executor.setThreadFactory(this.threadFactory);
            executor.setRejectedExecutionHandler(new CustomizableRejectedExecutionHandler());
            return executor;
        }
    }
}
