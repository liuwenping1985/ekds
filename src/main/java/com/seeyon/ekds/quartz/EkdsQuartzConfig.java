package com.seeyon.ekds.quartz;

import com.seeyon.ekds.quartz.job.FetchFileFromExternalJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by liuwenping on 2021/7/16.
 *
 * @Author liuwenping
 */
@Configuration
public class EkdsQuartzConfig {

    @Bean
    public JobDetail makeCommonJobDetail(){
        return JobBuilder.newJob(FetchFileFromExternalJob.class).withIdentity("FetchFileFromExternalJob").storeDurably().build();
    }

    @Bean
    public Trigger uploadTaskTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 23 * * ?");
        return TriggerBuilder.newTrigger().forJob(makeCommonJobDetail())
                .withIdentity("FetchFileFromExternalJob")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
