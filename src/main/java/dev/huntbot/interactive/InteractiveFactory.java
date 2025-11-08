package dev.huntbot.interactive;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;

public class InteractiveFactory {
    public static synchronized Interactive constructInteractive(
        SlashCommandInteractionEvent initEvent, Class<? extends Interactive> interactiveClass
    ) {
        if (interactiveClass == ConfirmInteractive.class) {
            return new ConfirmInteractive(initEvent);
        }

        throw new IllegalArgumentException("Not a valid interactive class: " + interactiveClass);
    }

    public static synchronized Interactive constructThreadInteractive(Interaction interaction, int manualPingIndex) {
        return new ThreadInteractive(interaction, manualPingIndex);
    }
}
