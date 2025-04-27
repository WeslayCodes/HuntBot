package dev.huntbot.commands.hunt;

import dev.huntbot.commands.Subcommand;
import dev.huntbot.util.logging.ExceptionHandler;
import dev.huntbot.util.time.TimeUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class BootsSubcommand extends Subcommand {
    public BootsSubcommand(SlashCommandInteractionEvent event) {
        super(event);
    }

    @Override
    public void execute() {
        long nextBootsSec = TimeUtil.getNextBootsSec();
        long lastBootsSec = TimeUtil.getLastBootsSec();
        long oneDaySec = TimeUtil.getOneDaySec();

        String bootsStr = lastBootsSec + oneDaySec > TimeUtil.getCurSec()
            ? STRS.getLoremasterActive().formatted(lastBootsSec + oneDaySec)
            : STRS.getLoremasterWaiting().formatted(nextBootsSec);

        this.event.getInteraction().reply(bootsStr).setEphemeral(true).queue(null,
            e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
        );
    }
}
