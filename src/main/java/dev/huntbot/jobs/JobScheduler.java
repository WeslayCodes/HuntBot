package dev.huntbot.jobs;

import dev.huntbot.util.logging.Log;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class JobScheduler {
    public static void scheduleJobs() {
        try {
            Log.debug(JobScheduler.class, "Scheduling jobs...");

            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.start();

            scheduler.scheduleJob(LogJob.getJob(), LogJob.getTrigger());
            scheduler.scheduleJob(ImageChangeJob.getJob(), ImageChangeJob.getTrigger());
            scheduler.scheduleJob(ClearProfileCacheJob.getJob(), ClearProfileCacheJob.getTrigger());

            Log.debug(JobScheduler.class, "Jobs successfully scheduled");
        } catch (SchedulerException exception) {
            Log.error(JobScheduler.class, "Failed to schedule one or more jobs", exception);
        }
    }
}
