package dev.huntbot.util.interaction;

import dev.huntbot.util.time.TimeUtil;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class InteractionUtil {
    private final static int CORE_POOL_SIZE = 10;
    public final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(CORE_POOL_SIZE);

    public final static Map<String, Long> usersOnCooldown = new HashMap<>();

    public final static int COOLDOWN_SLEEP_TIME = 10000;

    static {
        scheduler.schedule(() -> {
            for (String user : usersOnCooldown.keySet()) {
                if (usersOnCooldown.get(user) < TimeUtil.getCurMilli() - COOLDOWN_SLEEP_TIME) {
                    usersOnCooldown.remove(user);
                }
            }
        }, COOLDOWN_SLEEP_TIME, TimeUnit.MILLISECONDS);
    }

    public synchronized static int isOnCooldown(User user) {
        if (usersOnCooldown.containsKey(user.getId())) {
            return (int) (
                usersOnCooldown.get(user.getId()) + COOLDOWN_SLEEP_TIME - TimeUtil.getCurMilli() + 1000
            ) / 1000;
        }

        usersOnCooldown.put(user.getId(), TimeUtil.getCurMilli());
        scheduler.schedule(() -> usersOnCooldown.remove(user.getId()), COOLDOWN_SLEEP_TIME, TimeUnit.MILLISECONDS);

        return 0;
    }

    public static void shutdownScheduler() {
        scheduler.shutdown();
    }
}
