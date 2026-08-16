package dev.huntbot.interactive;

import dev.huntbot.bot.config.pings.IndivPingConfig;
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

    public static synchronized Interactive constructThreadInteractive(Interaction interaction, IndivPingConfig pingConfig) {
        return new ThreadInteractive(interaction, pingConfig);
    }
}
