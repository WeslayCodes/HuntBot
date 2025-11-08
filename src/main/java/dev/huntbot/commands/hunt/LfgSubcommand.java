package dev.huntbot.commands.hunt;

import dev.huntbot.api.util.Configured;
import dev.huntbot.commands.Subcommand;
import dev.huntbot.interactive.ConfirmInteractive;
import dev.huntbot.interactive.Interactive;
import dev.huntbot.interactive.InteractiveFactory;
import dev.huntbot.util.logging.ExceptionHandler;
import dev.huntbot.util.logging.Log;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LfgSubcommand extends Subcommand implements Configured {
    public LfgSubcommand(SlashCommandInteractionEvent event) {
        super(event);
    }

    @Override
    public void execute() {
        this.event.deferReply(true).queue(null, e -> ExceptionHandler.deferHandle(this.interaction, this, e));

        Interactive interactive = InteractiveFactory.constructInteractive(this.event, ConfirmInteractive.class);
        interactive.execute(null);
        Log.debug(this.user, this.getClass(), "Sent ConfirmInteractive");
    }
}
