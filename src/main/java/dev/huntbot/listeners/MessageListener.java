package dev.huntbot.listeners;

import com.google.gson.JsonObject;
import dev.huntbot.api.util.Configured;
import dev.huntbot.util.api.ApiRequest;
import dev.huntbot.util.logging.Log;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.http.HttpClient;

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
        boolean fromPingChannel = this.event.getMessage().getChannelId()
            .equals(CONFIG.getMainConfig().getPingChannel());
        boolean fromHunterChannel = this.event.getMessage().getChannelId()
            .equals(CONFIG.getMainConfig().getHunterChannel());
        boolean isPing = this.event.getMessage().getContentRaw().startsWith(STRS.getPromptInitString());
        boolean fromBot = this.event.getMessage().getAuthor().isBot();
        boolean shouldDeleteMessage = fromPingChannel && !fromBot;
        boolean shouldGenerateMessage = fromHunterChannel && !fromBot && isPing;

        if (shouldGenerateMessage) {
            try (HttpClient client = HttpClient.newHttpClient()) {
                this.event.getMessage().getChannel().sendTyping()
                    .queue(null, e -> Log.warn(this.getClass(), "Failed to send typing", e));
                String prompt = this.event.getMessage().getContentRaw().substring(STRS.getPromptInitString().length());
                JsonObject geminiResponse = ApiRequest.getGeneratedString(prompt, client);
                String textResponse = geminiResponse.getAsJsonArray("candidates").get(0).getAsJsonObject()
                    .getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject().get("text")
                    .getAsString();

                this.event.getMessage().reply(textResponse)
                    .queue(null, e -> Log.warn(this.getClass(), "Failed to reply to message", e));
            } catch (IOException exception) {
                Log.error(this.getClass(), "Failed to send/receive information", exception);
            } catch (InterruptedException exception) {
                Log.error(this.getClass(), "Send/receive was interrupted", exception);
            }

            return;
        }

        if (shouldDeleteMessage) {
            this.event.getMessage().delete().queue(null, e -> Log.warn(this.getClass(), "Failed to delete message", e));
        }
    }
}
