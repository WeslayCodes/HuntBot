package dev.huntbot.util.interaction;

import dev.huntbot.api.util.Configured;
import dev.huntbot.util.logging.ExceptionHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class SpecialReply implements Configured {
    public static void sendErrorMessage(IReplyCallback interaction, Object obj) {
        interaction.reply(STRS.getError()).setEphemeral(true).queue(null, e ->
            interaction.getHook().editOriginal(STRS.getError()).queue(null, e1 ->
                ExceptionHandler.handle(interaction.getUser(), obj.getClass(), e1)
            )
        );
    }

    public static void sendErrorMessage(InteractionHook hook, Object obj) {
        hook.sendMessage(STRS.getError()).setEphemeral(true)
            .queue(null, e -> ExceptionHandler.handle(hook.getInteraction().getUser(), obj.getClass(), e));
    }

    public static void sendErrorMessage(Message message, Object obj) {
        message.editMessage(STRS.getError())
            .queue(null, e -> ExceptionHandler.handle(obj.getClass(), e));
    }
}