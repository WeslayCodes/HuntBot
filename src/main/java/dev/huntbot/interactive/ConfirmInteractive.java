package dev.huntbot.interactive;

import dev.huntbot.bot.config.components.IndivComponentConfig;
import dev.huntbot.util.interactive.InteractiveUtil;
import dev.huntbot.util.interactive.StopType;
import dev.huntbot.util.logging.Log;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.*;

public class ConfirmInteractive extends UserInteractive {
    private final int manualPingIndex;
    private Boolean proceed;
    private GenericComponentInteractionCreateEvent curCompEvent;

    private final static Map<String, IndivComponentConfig> COMPONENTS = CONFIG.getComponentConfig().getConfirm();

    public ConfirmInteractive(SlashCommandInteractionEvent event) {
        super(event);

        this.manualPingIndex = event.getOption("reason") != null
            ? Objects.requireNonNull(event.getOption("reason")).getAsInt()
            : 0;

        Guild guild = event.getGuild();

        if (guild == null) {
            Log.error(this.getClass(), "Bad guild", new IllegalStateException());
        }
    }

    @Override
    public void execute(GenericComponentInteractionCreateEvent compEvent) {
        if (compEvent == null) {
            this.sendResponse();
            return;
        }

        this.curCompEvent = compEvent;

        String compID = compEvent.getComponentId().split(",")[1];

        Log.debug(
            this.user,
            this.getClass(),
            "Component: %s".formatted(compID)
        );

        switch (compID) {
            case "PROCEED" -> this.proceed = true;

            case "CANCEL" -> this.proceed = false;
        }

        this.sendResponse();
    }

    private void sendResponse() {
        if (this.proceed != null && this.proceed) {
            Interactive threadInteractive = InteractiveFactory
                .constructThreadInteractive(this.curCompEvent, manualPingIndex);
            threadInteractive.execute(null);
            Log.debug(this.user, this.getClass(), "Sent ThreadInteractive");
        }
        
        MessageEditBuilder editedMsg = new MessageEditBuilder()
            .setContent(this.getReplyString()).setComponents(this.getCurComponents());

        this.updateInteractive(false, editedMsg.build());

        if (this.proceed != null) {
            this.stop(StopType.FINISHED);
        }
    }

    private String getReplyString() {
        if (this.proceed == null) {
            return STRS.getManualPingConfirmations()[manualPingIndex];
        }

        if (this.proceed) {
            String roleId = CONFIG.getMainConfig().getManualPingRoles()[manualPingIndex];
            return STRS.getManualPingProceedResponse().formatted(roleId);
        }

        return STRS.getManualPingCancelResponse();
    }

    @Override
    public ActionRow[] getCurComponents() {
        List<ActionRowChildComponent> btns = InteractiveUtil.makeComponents(
            this.getInteractionID(), COMPONENTS.get("proceedBtn"), COMPONENTS.get("cancelBtn")
        );

        if (this.proceed == null) {
            return new ActionRow[] {
                ActionRow.of(btns)
            };
        }

        return new ActionRow[] {};
    }
}
