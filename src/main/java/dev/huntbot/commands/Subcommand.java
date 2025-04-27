package dev.huntbot.commands;

import dev.huntbot.api.util.Configured;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public abstract class Subcommand implements Configured {
    protected final SlashCommandInteractionEvent event;
    protected final SlashCommandInteraction interaction;
    protected final User user;

    public Subcommand(SlashCommandInteractionEvent event) {
        this.event = event;
        this.interaction = event.getInteraction();
        this.user = event.getUser();
    }

    public abstract void execute();
}
