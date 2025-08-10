package dev.huntbot.commands.hunt;

import dev.huntbot.api.util.Configured;
import dev.huntbot.commands.Subcommand;
import dev.huntbot.util.hunt.LfgReasonEnum;
import dev.huntbot.util.logging.ExceptionHandler;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.time.TimeUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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

        Guild guild = this.event.getGuild();

        if (guild == null) {
            Log.error(this.getClass(), "Bad guild", new IllegalStateException());
            return;
        }

        TextChannel pingChannel = guild.getTextChannelById(CONFIG.getMainConfig().getPingChannel());

        if (pingChannel == null) {
            Log.error(this.getClass(), "Bad ping channel", new IllegalStateException());
            return;
        }

        ThreadChannel threadChannel = guild.getThreadChannelById(CONFIG.getMainConfig().getThreads()[pingTypeRaw]);

        if (threadChannel == null) {
            Log.error(this.getClass(), "Bad thread: %s".formatted(pingType.toString()), new IllegalStateException());
            return;
        }

        String threadId = threadChannel.getId();
        String roleId = CONFIG.getMainConfig().getRoles()[pingTypeRaw];
        long timestamp = TimeUtil.getCurSec();
        String userId = this.user.getId();

        pingChannel.sendMessage(STRS.getLfgPing().formatted(roleId, threadId, timestamp)).queue(
            a -> threadChannel.sendMessage(STRS.getLfgThreadMsg().formatted(userId, userId)).queue(
                b -> this.event.reply(STRS.getLfgSuccess().formatted(threadId)).setEphemeral(true).queue(
                    null, e -> ExceptionHandler.replyHandle(this.event, LfgSubcommand.class, e)
                ),
                e -> ExceptionHandler.replyHandle(this.event, LfgSubcommand.class, e)
            ),
            e -> ExceptionHandler.replyHandle(this.event, LfgSubcommand.class, e)
        );

        Log.debug(this.user, this.getClass(), "Successfully pinged %s".formatted(pingType.toString()));
    }
}
