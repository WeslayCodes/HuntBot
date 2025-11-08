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
        TextChannel pingChannel = event.getJDA().getTextChannelById(CONFIG.getMainConfig().getPingChannel());
        TextChannel threadSpyChannel = event.getJDA().getTextChannelById(CONFIG.getMainConfig().getThreadSpyChannel());

        if (logChannel == null) {
            Log.warn(this.getClass(), "Invalid log channel ID. Channel logs are disabled!");
        }

        if (pingChannel == null) {
            Log.warn(this.getClass(), "Invalid ping channel ID. Ping command is disabled!");
        }

        if (threadSpyChannel == null) {
            Log.warn(this.getClass(), "Invalid thread spy channel ID. Thread spying is disabled!");
        }

        Log.info(this.getClass(), "Bot is online!", true);

        JobScheduler.scheduleJobs();
    }
}
