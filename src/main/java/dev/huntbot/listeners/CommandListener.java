package dev.huntbot.listeners;

import dev.huntbot.HuntBotApp;
import dev.huntbot.api.util.Configured;
import dev.huntbot.commands.Subcommand;
import dev.huntbot.util.interaction.InteractionUtil;
import dev.huntbot.util.interaction.SpecialReply;
import dev.huntbot.util.logging.ExceptionHandler;
import dev.huntbot.util.logging.Log;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class CommandListener extends ListenerAdapter implements Runnable, Configured {
    private final Map<String, Constructor<? extends Subcommand>> subcommands = HuntBotApp.getBot().getSubcommands();
    private SlashCommandInteractionEvent event = null;

    public CommandListener() {
        super();
    }

    public CommandListener(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        new Thread(new CommandListener(event)).start();
    }

    @Override
    public void run() {
        if (!this.event.isFromGuild()) {
            return;
        }

        int cooldownSeconds = InteractionUtil.isOnCooldown(this.event.getUser());

        if (cooldownSeconds > 0) {
            this.event.getInteraction().reply(STRS.getOnCooldown().formatted(cooldownSeconds)).setEphemeral(true).queue(
                null, e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
            );
            return;
        }

        String commandStr = "/%s %s".formatted(this.event.getName(), this.event.getSubcommandName());
        Log.debug(this.event.getUser(), this.getClass(), "Running %s".formatted(commandStr));

        try {
            Subcommand subcommand = subcommands.get(this.event.getName() + this.event.getSubcommandName())
                .newInstance(this.event);
            subcommand.execute();

            Log.debug(this.event.getUser(), this.getClass(), "Finished processing %s".formatted(commandStr));
        } catch (InstantiationException exception) {
            SpecialReply.sendErrorMessage(this.event.getInteraction(), this);
            Log.error(
                this.event.getUser(),
                this.getClass(),
                "%s's class is an abstract class".formatted(commandStr),
                exception
            );
        } catch (IllegalAccessException exception) {
            SpecialReply.sendErrorMessage(this.event.getInteraction(), this);
            Log.error(
                this.event.getUser(),
                this.getClass(),
                "%s's constructor is not public".formatted(commandStr),
                exception
            );
        } catch (InvocationTargetException exception) {
            SpecialReply.sendErrorMessage(this.event.getInteraction(), this);
            Log.error(
                this.event.getUser(),
                this.getClass(),
                "%s's constructor threw exception".formatted(commandStr),
                exception
            );
        } catch (RuntimeException exception) {
            SpecialReply.sendErrorMessage(this.event.getInteraction(), this);
            Log.error(
                this.event.getUser(),
                this.getClass(),
                "%s's execute method threw a runtime exception".formatted(commandStr),
                exception
            );
        }
    }
}
