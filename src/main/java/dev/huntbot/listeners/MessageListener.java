package dev.huntbot.listeners;

import dev.huntbot.api.util.Configured;
import dev.huntbot.util.logging.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter implements Runnable, Configured {
    private MessageReceivedEvent event = null;
    private static final String threadSpyChannelId = CONFIG.getMainConfig().getThreadSpyChannel();
    private static TextChannel threadSpyChannel;

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
        boolean fromPingChannelThread = this.event.getChannel().getType().isThread() &&
            this.event.getChannel().asThreadChannel().getParentChannel().getId()
                .equals(CONFIG.getMainConfig().getPingChannel());
        boolean fromBot = this.event.getMessage().getAuthor().isBot();
        boolean shouldDeleteMessage = fromPingChannel && !fromBot;

        if (shouldDeleteMessage) {
            this.event.getMessage().delete().queue(null, e ->
                Log.warn(MessageListener.class, "Failed to delete message", e)
            );
        }

        if (fromPingChannelThread) {
            if (threadSpyChannel == null) {
                threadSpyChannel = this.event.getJDA().getTextChannelById(threadSpyChannelId);
            }

            String authorStr = this.event.getAuthor().getName() + " (%s)".formatted(this.event.getAuthor().getId());
            String messageStr = this.event.getMessage().getContentRaw();

            EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(authorStr, null, this.event.getAuthor().getEffectiveAvatarUrl())
                .setTitle(this.event.getChannel().getName())
                .setDescription(messageStr.substring(0, Math.min(4096, messageStr.length())) +
                    "\n\n" + this.event.getMessage().getJumpUrl());

            try (MessageCreateData msg = new MessageCreateBuilder().setEmbeds(embed.build()).build()) {
                threadSpyChannel.sendMessage(msg).queue(
                    m -> {},
                    e -> Log.error(MessageListener.class, "Failed to forward thread message", e)
                );
            }
        }
    }
}
