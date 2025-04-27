package dev.huntbot.commands.hunt;

import dev.huntbot.api.util.Configured;
import dev.huntbot.commands.Subcommand;
import dev.huntbot.util.hunt.LfgReasonEnum;
import dev.huntbot.util.logging.ExceptionHandler;
import dev.huntbot.util.logging.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class LfgSubcommand extends Subcommand implements Configured {
    public LfgSubcommand(SlashCommandInteractionEvent event) {
        super(event);
    }

    @Override
    public void execute() {
        int pingTypeRaw = Objects.requireNonNull(this.event.getOption("reason")).getAsInt();
        LfgReasonEnum pingType = LfgReasonEnum.values()[pingTypeRaw];
        String ign = Objects.requireNonNull(this.event.getOption("ign")).getAsString();

        Guild guild = this.event.getGuild();

        if (guild == null) {
            Log.error(this.getClass(), "Bad guild", new IllegalStateException());
            return;
        }

        ThreadChannel threadChannel = guild.getThreadChannelById(CONFIG.getMainConfig().getThreads()[pingTypeRaw]);

        if (threadChannel == null) {
            Log.error(this.getClass(), "Bad thread: %s".formatted(pingType.toString()), new IllegalStateException());
            return;
        }

        String roleId = CONFIG.getMainConfig().getRoles()[pingTypeRaw];

        threadChannel.sendMessage(STRS.getLfgPing().formatted(roleId, ign)).queue(
            a -> this.event.reply(STRS.getLfgSuccess().formatted(threadChannel)).setEphemeral(true).queue(
                null, e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
            ),
            e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
        );

        Log.debug(this.user, this.getClass(), "Successfully pinged %s".formatted(pingType.toString()));
    }
}
