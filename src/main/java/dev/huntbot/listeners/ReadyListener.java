package dev.huntbot.listeners;

import dev.huntbot.api.util.Configured;
import dev.huntbot.jobs.JobScheduler;
import dev.huntbot.util.logging.Log;
import lombok.Getter;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyListener extends ListenerAdapter implements Configured {
    @Getter private static boolean done = false;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ReadyListener.done = true;

        TextChannel logChannel = event.getJDA().getTextChannelById(CONFIG.getMainConfig().getLogChannel());

        if (logChannel == null) {
            Log.warn(this.getClass(), "Invalid log channel ID. Channel logs are disabled!");
        }

        Log.info(this.getClass(), "Bot is online!", true);

        JobScheduler.scheduleJobs();
    }
}
