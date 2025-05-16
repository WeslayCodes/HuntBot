package dev.huntbot.jobs;

import com.google.gson.JsonObject;
import dev.huntbot.HuntBotApp;
import dev.huntbot.api.util.Configured;
import dev.huntbot.util.api.ApiRequest;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.time.TimeUtil;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.quartz.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.TimeZone;

public class UploadTrackerJob implements Job, Configured {
    @Getter private final static JobDetail job = JobBuilder.newJob(UploadTrackerJob.class).build();
    @Getter private final static Trigger trigger = TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 ? * *").inTimeZone(TimeZone.getTimeZone("UTC"))).build();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Guild guild = HuntBotApp.getBot().getJDA().getGuildById(CONFIG.getMainConfig().getGuild());

        if (guild == null) {
            Log.error(
                ImageChangeJob.class, "Unable to find guild. Could not change images!", new IllegalArgumentException()
            );
            return;
        }

        ThreadChannel uploadTrackerThread = guild.getThreadChannelById(CONFIG.getMainConfig().getUploadChannel());

        if (uploadTrackerThread == null) {
            Log.error(
                ImageChangeJob.class, "Invalid upload tracker thread ID", new IllegalArgumentException()
            );
            return;
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            JsonObject lastUpload = ApiRequest.getLastVideo(client);
            String timestamp = lastUpload.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("snippet")
                .get("publishedAt").getAsString();
            long timestampMillis = Instant.parse(timestamp).toEpochMilli();
            long daysSince = (TimeUtil.getCurMilli() - timestampMillis) / TimeUtil.getOneDayMilli();
            String prompt = STRS.getGeminiPigMessage().formatted(daysSince, daysSince);

            JsonObject geminiResponse = ApiRequest.getGeneratedString(prompt, client);
            String textResponse = geminiResponse.getAsJsonArray("candidates").get(0).getAsJsonObject()
                .getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject().get("text").getAsString();

            uploadTrackerThread.sendMessage(textResponse).queue();
        } catch (IOException exception) {
            Log.error(this.getClass(), "Failed to send/receive information", exception);
        } catch (InterruptedException exception) {
            Log.error(this.getClass(), "Send/receive was interrupted", exception);
        }
    }
}
