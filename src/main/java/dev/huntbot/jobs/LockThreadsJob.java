package dev.huntbot.jobs;

import dev.huntbot.HuntBotApp;
import dev.huntbot.api.util.Configured;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.time.TimeUtil;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.quartz.*;

import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LockThreadsJob implements Job, Configured {
    @Getter
    private final static JobDetail job = JobBuilder.newJob(LockThreadsJob.class).build();
    @Getter private final static Trigger trigger = TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule("0 * * ? * *").inTimeZone(TimeZone.getTimeZone("UTC"))).build();
    private final static Guild guild = HuntBotApp.getBot().getJDA().getGuildById(CONFIG.getMainConfig().getGuild());

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (guild == null) {
            Log.warn(this.getClass(), "Invalid guild ID.");
            return;
        }

        guild.retrieveActiveThreads().queue(
            threads -> getUnlockedThreads(threads).forEach(this::lockThread),
            e -> Log.error(this.getClass(), "Failed to retrieve active threads", e)
        );
    }

    private Stream<ThreadChannel> getUnlockedThreads(List<ThreadChannel> threads) {
        return threads.stream().filter(
            thread -> thread.getParentChannel().getId()
                .equals(CONFIG.getMainConfig().getPingChannel()) && !thread.isLocked()
        );
    }

    private void lockThread(ThreadChannel thread) {
        thread.retrieveParentMessage().queue(
            message -> {
                Matcher matcher = Pattern.compile(STRS.getPingTimestampRegex()).matcher(message.getContentRaw());

                if (matcher.find()) {
                    long timestamp = Long.parseLong(matcher.group(1));

                    if (timestamp > TimeUtil.getCurSec()) {
                        return;
                    }

                    timestamp = TimeUtil.getCurSec();

                    String newMessage = message.getContentRaw().replaceFirst("(?s)\n.*", "") +
                        STRS.getPingLockedStr().formatted(timestamp);

                    try (MessageEditData editData = new MessageEditBuilder().setContent(newMessage).build()) {
                        message.editMessage(editData).queue(
                            t -> Log.info(this.getClass(), "Edited " + thread.getName()),
                            e -> Log.error(this.getClass(), "Failed to edit " + thread.getName(), e)
                        );
                    }

                    thread.getManager().setLocked(true).queue(
                        t -> Log.info(this.getClass(), "Locked " + thread.getName()),
                        e -> Log.error(this.getClass(), "Failed to lock " + thread.getName(), e)
                    );
                }
            },
            e -> Log.error(this.getClass(), "Failed to retrieve parent message", e)
        );
    }
}
