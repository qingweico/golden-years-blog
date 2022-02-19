package cn.qingweico.user.task;

import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * @author:qiming
 * @date: 2021/10/24
 */
@Configuration
@EnableScheduling
public class TaskDeleteLoginLog {
    final Logger log = LoggerFactory.getLogger(TaskDeleteLoginLog.class);
    @Resource
    private LoginLogService loginLogService;

    @Scheduled(cron = "0 0 0 ? 1/1 ?")
    public void cleanLoginLog() {
        loginLogService.cleanLoginLog();
        log.info("{}, 执行登陆日志清理", DateUtils.getCurrentDateString());
    }
}
