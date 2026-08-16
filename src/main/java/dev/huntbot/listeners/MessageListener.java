package dev.huntbot.listeners;

import dev.huntbot.api.util.Configured;
import dev.huntbot.util.logging.Log;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter implements Runnable, Configured {
    private MessageReceivedEvent event = null;

    public MessageListener() {
        super();
    }

    public MessageListener(MessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        new Thread(new MessageListener(event)).start();
    }

    @Override
    public void run() {
        boolean fromPingChannel = this.event.getChannel().getId()
            .equals(CONFIG.getMainConfig().getPingChannel());
        boolean fromBot = this.event.getMessage().getAuthor().isBot();
        boolean shouldDeleteMessage = fromPingChannel && !fromBot;

        if (shouldDeleteMessage) {
            this.event.getMessage().delete().queue(null, e ->
                Log.warn(MessageListener.class, "Failed to delete message", e)
            );
        }
    }
}
