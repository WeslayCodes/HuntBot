package dev.huntbot.jobs;

import dev.huntbot.listeners.AutofillListener;
import dev.huntbot.util.time.TimeUtil;
import lombok.Getter;
import org.quartz.*;

import java.util.TimeZone;

public class ClearProfileCacheJob implements Job {
    @Getter
    private final static JobDetail job = JobBuilder.newJob(ClearProfileCacheJob.class).build();
    @Getter private final static Trigger trigger = TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 ? * *").inTimeZone(TimeZone.getTimeZone("UTC"))).build();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        AutofillListener.userProfiles.entrySet()
            .removeIf(entry -> entry.getValue().timestamp() < TimeUtil.getCurMilli() - TimeUtil.getOneDaySec());
    }
}
