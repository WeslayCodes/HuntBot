package dev.huntbot.interactive;

import dev.huntbot.bot.config.components.IndivComponentConfig;
import dev.huntbot.util.interactive.InteractiveUtil;
import dev.huntbot.util.interactive.StopType;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.time.TimeUtil;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.*;

public class ThreadInteractive extends UserInteractive {
    private final int manualPingIndex;
    private boolean lock = false;
    private Message message;

    private final static Map<String, IndivComponentConfig> COMPONENTS = CONFIG.getComponentConfig().getThread();

    public ThreadInteractive(Interaction interaction, int manualPingIndex) {
        super(interaction, true, CONSTANTS.getThreadInteractiveStop(), CONSTANTS.getThreadInteractiveStop());
        this.manualPingIndex = manualPingIndex;
    }

    @Override
    public void execute(GenericComponentInteractionCreateEvent compEvent) {
        if (compEvent == null) {
            this.sendResponse();
            return;
        }

        String compID = compEvent.getComponentId().split(",")[1];

        Log.debug(
            this.user,
            this.getClass(),
            "Component: %s".formatted(compID)
        );

        if (compID.equals("LOCK")) {
            this.lock = true;
            this.message = compEvent.getMessage();
        }

        this.sendResponse();
    }

    private void sendResponse() {
        String roleId = CONFIG.getMainConfig().getManualPingRoles()[manualPingIndex];
        String userId = this.user.getId();
        long timestamp = TimeUtil.getPriorMinuteSecs(TimeUtil.getCurSec() +
            CONSTANTS.getManualPingLockMaxDelaySeconds()[manualPingIndex]);
        String messageStr = STRS.getManualPingMessages()[manualPingIndex].formatted(roleId, userId, userId) +
            STRS.getPingUnlockedStr().formatted(timestamp);
        String threadName = STRS.getManualPingThreadNames()[manualPingIndex]
            .formatted(this.user.getEffectiveName());

        if (this.lock) {
            ThreadChannel thread = this.message.getStartedThread();
            timestamp = TimeUtil.getCurSec();

            messageStr = message.getContentRaw().replaceFirst("(?s)\n.*", "") +
                STRS.getPingLockedStr().formatted(timestamp);

            if (thread != null && !thread.isLocked()) {
                thread.getManager().setLocked(true).queue(
                    t -> Log.info(this.getClass(), "Locked " + thread.getName()),
                    e -> Log.error(this.getClass(), "Failed to lock " + thread.getName(), e)
                );
            }
        }
        
        MessageEditBuilder editedMsg = new MessageEditBuilder()
            .setContent(messageStr)
            .setAllowedMentions(EnumSet.of(Message.MentionType.ROLE))
            .mentionRoles(roleId)
            .setComponents(this.getCurComponents());

        if (this.lock) {
            this.updateInteractive(false, editedMsg.build());
            this.stop(StopType.FINISHED);
        } else {
            this.updateInteractiveWithThead(false, editedMsg.build(), threadName);
        }
    }

    @Override
    public ActionRow[] getCurComponents() {
        List<ActionRowChildComponent> btns = InteractiveUtil.makeComponents(
            this.getInteractionID(), COMPONENTS.get("lockBtn")
        );

        if (!this.lock) {
            return new ActionRow[] {
                ActionRow.of(btns)
            };
        }

        return new ActionRow[] {};
    }
}
