package cn.qingweico.user.task;

import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * @author zqw
 * @date 2021/10/24
 */
@Configuration
@EnableScheduling
@Slf4j
public class TaskDeleteLoginLog {
    @Resource
    private LoginLogService loginLogService;

    @Scheduled(cron = "0 0 0 ? 1/1 ?")
    public void cleanLoginLog() {
        loginLogService.cleanLoginLog();
        log.info("{}, 执行登陆日志清理", DateUtils.getCurrentDateString());
    }
}
