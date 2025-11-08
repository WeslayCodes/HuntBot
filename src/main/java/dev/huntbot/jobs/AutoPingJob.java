package dev.huntbot.jobs;

import dev.huntbot.HuntBotApp;
import dev.huntbot.api.util.Configured;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.time.TimeUtil;
import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;

public class AutoPingJob implements Job, Configured {
    @Getter private final static JobDetail job = JobBuilder.newJob(AutoPingJob.class).build();
    @Getter private final static List<Trigger> triggers = new ArrayList<>();

    static {
        for (int i=0; i<CONSTANTS.getAutoPingCronStrs().length; i++) {
            String schedule = CONSTANTS.getAutoPingCronStrs()[i];
            triggers.add(TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(schedule))
                .withIdentity(String.valueOf(i)).build());
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int autoPingIndex = Integer.parseInt(context.getTrigger().getKey().getName());

        TextChannel pingChannel = HuntBotApp.getBot().getJDA()
            .getChannelById(TextChannel.class, CONFIG.getMainConfig().getPingChannel());

        if (pingChannel == null) {
            Log.warn(this.getClass(), "Failed to find ping channel");
            return;
        }

        String roleId = CONFIG.getMainConfig().getAutoPingRoles()[autoPingIndex];
        long timestamp = TimeUtil.getPriorMinuteSecs(TimeUtil.getCurSec() +
            CONSTANTS.getAutoPingLockMaxDelaySeconds()[autoPingIndex]);

        String threadMsgStr = STRS.getAutoPingMessages()[autoPingIndex].formatted(roleId, timestamp) +
            STRS.getPingUnlockedStr().formatted(timestamp);
        String threadName = STRS.getAutoPingThreadNames()[autoPingIndex];

        pingChannel.sendMessage(threadMsgStr).queue(
            msg -> msg.createThreadChannel(threadName).queue(
                threadChannel -> {},
                e -> Log.error(this.getClass(), "Failed to create thread", e)
            ),
            e -> Log.error(this.getClass(), "Failed to send initial thread message", e)
        );
    }
}
