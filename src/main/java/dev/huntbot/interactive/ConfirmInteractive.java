package dev.huntbot.interactive;

import dev.huntbot.bot.config.components.IndivComponentConfig;
import dev.huntbot.bot.config.pings.IndivPingConfig;
import dev.huntbot.util.data.pingrequest.PingRequestUtil;
import dev.huntbot.util.interactive.InteractiveUtil;
import dev.huntbot.util.interactive.StopType;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.time.TimeUtil;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.*;

public class ConfirmInteractive extends UserInteractive {
    private final String pingId;
    private final IndivPingConfig pingConfig;
    private final boolean isDemandBased;
    private Boolean proceed;
    private GenericComponentInteractionCreateEvent curCompEvent;

    private final static Map<String, IndivComponentConfig> COMPONENTS = CONFIG.getComponentConfig().getConfirm();

    public ConfirmInteractive(SlashCommandInteractionEvent event) {
        super(event);

        this.pingId = Objects.requireNonNull(event.getOption("reason")).getAsString();
        Map<String, IndivPingConfig> pingConfigs = CONFIG.getPingConfig().getManual().containsKey(this.pingId)
            ? CONFIG.getPingConfig().getManual()
            : CONFIG.getPingConfig().getDemandBased();

        this.pingConfig = pingConfigs.get(this.pingId);
        this.isDemandBased = this.pingConfig.getRequiredRequests() > 0;

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
        if (this.proceed != null && this.proceed && this.isDemandBased) {
            this.sendDemandBasedResponse();
            return;
        }

        if (this.proceed != null && this.proceed) {
            this.sendManualResponse();
            return;
        }

        String replyString = this.proceed == null
            ? this.pingConfig.getConfirmationMessage()
            : this.pingConfig.getCancelledMessage().formatted(this.pingConfig.getRoleId());

        this.finishResponse(replyString);
    }

    private void sendDemandBasedResponse() {
        PingRequestUtil.merge(this.user.getId(), this.pingId)
            .thenCompose(v -> {
                long demandStartTimestamp = TimeUtil.getCurMilli() - this.pingConfig.getRequestExpirationSeconds() * 1000L;
                return PingRequestUtil.demandGet(this.pingId, demandStartTimestamp);
            })
            .thenAccept(demandAmount -> {
                long requestEndTimestamp = TimeUtil.getCurSec() + this.pingConfig.getRequestExpirationSeconds();
                String reply = this.pingConfig.getSuccessMessage().formatted(
                    this.pingConfig.getRoleId(), demandAmount, this.pingConfig.getRequiredRequests(), requestEndTimestamp);
                this.finishResponse(reply);

                if (demandAmount >= this.pingConfig.getRequiredRequests()) {
                    Interactive threadInteractive = InteractiveFactory
                        .constructThreadInteractive(this.curCompEvent, this.pingConfig);
                    threadInteractive.execute(null);
                    PingRequestUtil.demandClear(this.pingId)
                        .exceptionally(ex -> {
                            Log.error(this.user, this.getClass(), "Failed to clear demand for %s".formatted(this.pingId), ex);
                            return null;
                        });
                }

                Log.debug(this.user, this.getClass(), "Sent request for %s".formatted(this.pingId));
            })
            .exceptionally(ex -> {
                Log.error(this.user, this.getClass(), "Failed to process demand based ping for %s".formatted(this.pingId), ex);
                this.stop(StopType.EXCEPTION);
                return null;
            });
    }

    private void sendManualResponse() {
        Interactive threadInteractive = InteractiveFactory
            .constructThreadInteractive(this.curCompEvent, this.pingConfig);
        threadInteractive.execute(null);

        Log.debug(this.user, this.getClass(), "Sent ThreadInteractive");
        this.finishResponse(this.pingConfig.getSuccessMessage().formatted(this.pingConfig.getRoleId()));
    }

    private void finishResponse(String replyString) {
        MessageEditBuilder editedMsg = new MessageEditBuilder()
            .setContent(replyString).setComponents(this.getCurComponents());

        this.updateInteractive(false, editedMsg.build());

        if (this.proceed != null) {
            this.stop(StopType.FINISHED);
        }
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
