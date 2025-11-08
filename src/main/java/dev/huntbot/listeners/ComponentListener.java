package dev.huntbot.listeners;

import dev.huntbot.HuntBotApp;
import dev.huntbot.interactive.Interactive;
import dev.huntbot.util.interactive.StopType;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.time.TimeUtil;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ComponentListener extends ListenerAdapter implements Runnable {
    private GenericComponentInteractionCreateEvent event = null;

    public ComponentListener() {
        super();
    }

    public ComponentListener(GenericComponentInteractionCreateEvent event) {
        this.event = event;
    }

    @Override
    public void onGenericComponentInteractionCreate(@NotNull GenericComponentInteractionCreateEvent event) {
        new Thread(new ComponentListener(event)).start();
    }

    @Override
    public void run() {
        String[] componentID = this.event.getComponentId().split(",");
        String interactiveBaseID = componentID[0];
        Interactive interactive = HuntBotApp.getBot().getInteractives().get(
            interactiveBaseID + "," + this.event.getUser().getId()
        );
        long startTime = TimeUtil.getCurMilli();

        if (interactive == null) {
            return;
        }

        Log.debug(
            this.event.getUser(),
            this.getClass(),
            "Pressed %s in %s".formatted(componentID[1], interactive.getClass().getSimpleName())
        );

        try {
            interactive.attemptExecute(this.event, startTime);
            Log.debug(
                this.event.getUser(),
                this.getClass(),
                "Finished processing %s in %s".formatted(componentID[1], interactive.getClass().getSimpleName())
            );
        } catch (RuntimeException exception) {
            interactive.stop(StopType.EXCEPTION);
            Log.error(
                this.event.getUser(),
                this.getClass(),
                "%s threw a runtime exception".formatted(interactive.getClass().getSimpleName()),
                exception
            );
        }
    }
}