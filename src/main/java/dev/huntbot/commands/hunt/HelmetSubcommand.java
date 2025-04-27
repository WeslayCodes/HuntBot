package dev.huntbot.commands.hunt;

import dev.huntbot.commands.Subcommand;
import dev.huntbot.util.logging.ExceptionHandler;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.time.TimeUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class HelmetSubcommand extends Subcommand {
    public HelmetSubcommand(SlashCommandInteractionEvent event) {
        super(event);
    }

    @Override
    public void execute() {
        String ign = Objects.requireNonNull(this.event.getOption("ign")).getAsString();
        int days = TimeUtil.getDayOfYear();

        int donationNum = -2 * (14742114 - (21408112 + days * days) + 29 * ign.length());
        String helmetStr = STRS.getHelmetAmount().formatted(ign, donationNum);

        this.event.getInteraction().reply(helmetStr).setEphemeral(true).queue(null,
            e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
        );

        Log.debug(this.user, this.getClass(), "Helmet value: " + donationNum);
    }
}
