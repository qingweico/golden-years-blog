package cn.qingweico.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Objects;

/**
 * 任务调度
 *
 * @author zqw
 * @date 2020/10/8
 */
@Configuration
public class QuartzConfiguration {
    private final MethodInvokingJobDetailFactoryBean jobDetailFactory;
    private final CronTriggerFactoryBean cronTriggerFactoryBean;

    @Autowired
    public QuartzConfiguration(MethodInvokingJobDetailFactoryBean jobDetailFactory, CronTriggerFactoryBean cronTriggerFactoryBean) {
        this.jobDetailFactory = jobDetailFactory;
        this.cronTriggerFactoryBean = cronTriggerFactoryBean;
    }

    /**
     * 创建jobDetailFactory并返回
     *
     * @return MethodInvokingJobDetailFactoryBean
     */
    @Bean(name = "jobDetailFactory")
    public MethodInvokingJobDetailFactoryBean createJobDetail() {
        MethodInvokingJobDetailFactoryBean jobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
        jobDetailFactoryBean.setName("");
        jobDetailFactoryBean.setGroup("");
        jobDetailFactoryBean.setConcurrent(false);
        jobDetailFactoryBean.setTargetObject(null);
        jobDetailFactoryBean.setTargetMethod("dailyCalculate");
        return jobDetailFactoryBean;
    }

    /**
     * 创建cronTriggerFactory并返回
     *
     * @return CronTriggerFactoryBean
     */
    @Bean("productSellDailyTriggerFactory")
    public CronTriggerFactoryBean createProductSellDailyTrigger() {
        CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
        triggerFactory.setName("");
        triggerFactory.setGroup("");
        triggerFactory.setJobDetail(Objects.requireNonNull(jobDetailFactory.getObject()));
        triggerFactory.setCronExpression("0 0 0 * * ? *");
        return triggerFactory;
    }

    /**
     * 创建调度工厂并返回
     *
     * @return SchedulerFactoryBean
     */
    @Bean("schedulerFactory")
    public SchedulerFactoryBean createSchedulerFactory() {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setTriggers(cronTriggerFactoryBean.getObject());
        return schedulerFactory;
    }
}
