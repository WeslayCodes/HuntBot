package dev.huntbot.jobs;

import dev.huntbot.util.logging.Log;
import lombok.Getter;
import org.quartz.*;

public class LogJob implements Job {
    @Getter private final static JobDetail job = JobBuilder.newJob(LogJob.class).build();
    @Getter private final static Trigger trigger = TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule("0 */5 * ? * *")).build();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String logString = "Memory Used: %,dMB/%,dMB | Threads: %,d";

        Log.debug(
            LogJob.class,
            logString.formatted(
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024),
                (Runtime.getRuntime().maxMemory()) / (1024 * 1024),
                Thread.activeCount()
            )
        );
    }
}
