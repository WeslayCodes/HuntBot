package dev.huntbot.commands.hunt;

import dev.huntbot.commands.Subcommand;
import dev.huntbot.util.logging.ExceptionHandler;
import dev.huntbot.util.logging.Log;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.Objects;

public class SailorSubcommand extends Subcommand {
    public SailorSubcommand(SlashCommandInteractionEvent event) {
        super(event);
    }

    @Override
    public void execute() {
        Member member = Objects.requireNonNull(this.event.getMember());
        String memberNickname = member.getEffectiveName();

        if (memberNickname.endsWith(STRS.getSailorEmoticon())) {
            try {
                member.modifyNickname(memberNickname.substring(0, memberNickname.indexOf(STRS.getSailorEmoticon())))
                    .queue(a -> this.event.reply(STRS.getSailorRemoved()).setEphemeral(true).queue(null,
                        e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
                    ));
            } catch (HierarchyException exception) {
                this.event.reply(STRS.getSailorFailed()).setEphemeral(true).queue(null,
                    e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
                );
            }
        } else {
            try {
                member.modifyNickname(memberNickname + STRS.getSailorEmoticon())
                    .queue(a -> this.event.reply(STRS.getSailorAdded()).setEphemeral(true).queue(null,
                        e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
                    ));
            } catch (HierarchyException exception) {
                this.event.reply(STRS.getSailorFailed()).setEphemeral(true).queue(null,
                    e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
                );
            }
        }

        Log.debug(this.user, this.getClass(), "Successfully updated nickname");
    }
}
