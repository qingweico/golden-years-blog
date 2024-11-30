package cn.qingweico.core.config;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zqw
 * @date 2022/4/4
 */
@Component
public class DruidMonitor {

    private static final Logger log = LoggerFactory.getLogger(DruidMonitor.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    /**
     * 是否开启
     */
    @Value("${druid.monitor.enabled:true}")
    private Boolean druidMonitor;

    /**
     * 延时启动
     */
    @Value("${druid.monitor.delay.seconds:30}")
    private int delay;

    /**
     * 限制行数
     */
    @Value("${druid.monitor.sql.fetch.row:1000}")
    private int fetchRow;

    /**
     * 限制时间
     */
    @Value("${druid.monitor.sql.fetch.duration:800}")
    private int fetchDuration;

    public DruidMonitor() {
    }

    @PostConstruct
    public void init() {
        this.start();
    }

    private void start() {
        SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
            try {
                this.druidMonitor();
            } catch (Exception e) {
                log.error("druid_monitor_error:{}", e.getMessage());
            }
        }, 1, delay, TimeUnit.SECONDS);
    }

    private void druidMonitor() {
        if (!Boolean.TRUE.equals(this.druidMonitor)) {
            log.warn("druid.monitor.enabled={}", this.druidMonitor);
        } else {
            DruidStatManagerFacade druidstatManagerFacade = DruidStatManagerFacade.getInstance();
            if (druidstatManagerFacade == null) {
                log.warn("druidStatManagerFacade is null !");
            } else {
                druidstatManagerFacade.getDataSourceStatDataList(true).forEach((objectMap) -> {
                    // 数据源层面监控
                    Integer identity = (Integer) objectMap.get("Identity");
                    String jdbc = Strings.nullToEmpty(StringUtils.substringBefore(String.valueOf(objectMap.get("URL")), "?"));
                    this.dataSourceMonitor(objectMap, jdbc);

                    // SQL层面监控
                    List<Map<String, Object>> list = druidstatManagerFacade.getSqlStatDataList(identity);
                    this.sqlMonitor(list, jdbc);
                });
                druidstatManagerFacade.resetSqlStat();
            }

        }
    }


    private void dataSourceMonitor(Map<String, Object> objectMap, String jdbc) {
        Integer maxActive = Optional.ofNullable((Integer) objectMap.get("MaxActive")).orElse(0);
        Integer activeCount = Optional.ofNullable((Integer) objectMap.get("ActiveCount")).orElse(0);
        Integer waitThreadCount = Optional.ofNullable((Integer) objectMap.get("WaitThreadCount")).orElse(0);
        if (activeCount > maxActive) {
            log.error("druid_wait_thread_count:{}", waitThreadCount);
            log.error("druid_connect_full:{},{}", jdbc, JSON.toJSON(objectMap));
        }
    }

    private void sqlMonitor(List<Map<String, Object>> list, String jdbc) {
        if (list != null && !list.isEmpty()) {
            list.forEach((map) -> {
                Long fetchRowCountMax = (Long) map.getOrDefault("FetchRowCountMax", 0L);
                Long maxTimespan = (Long) map.getOrDefault("MaxTimespan", 0L);
                Long errorCount = (Long) map.getOrDefault("ErrorCount", 0L);
                String sql;
                // 超出行限制
                if (errorCount > 0L || fetchRowCountMax > (long) this.fetchRow) {
                    sql = map.getOrDefault("SQL", "").toString().replace("\n", "");
                    log.warn("sqlMonitor_warn:fetchRowCountMax={},maxTimespan={},errorCount={},jdbc={},fetch_row_{}={},{}", fetchRowCountMax, maxTimespan, errorCount, jdbc, this.fetchRow, sql, JSON.toJSON(map));
                    // 超出耗时限制
                    if (maxTimespan > fetchDuration) {
                        log.warn("sqlMonitor_warn:fetchRowCountMax={},maxTimespan={},errorCount={},jdbc={},fetch_row_{}={},{}", fetchRowCountMax, maxTimespan, errorCount, jdbc, this.fetchRow, sql, JSON.toJSON(map));
                    }
                    // 开启debug 打印详细信息
                    if (log.isDebugEnabled()) {
                        log.debug("sqlMonitor_warn:fetchRowCountMax={},maxTimespan={},errorCount={},jdbc={},fetch_row_{}={},{}", fetchRowCountMax, maxTimespan, errorCount, jdbc, this.fetchRow, sql, JSON.toJSON(map));
                    }
                }
            });
        } else {
            log.debug("sqlMonitor_list_empty:{}", jdbc);
        }
    }
}
