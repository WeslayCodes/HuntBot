package dev.huntbot.bot.config.pings;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IndivPingConfig {
    @NonNull private String roleId = "";
    private int lockDelaySeconds = 0;
    @NonNull private String threadStartMessage = "";
    @NonNull private String threadName = "";

    // Manual/Demand-based
    private String cancelledMessage;
    private String successMessage;
    private String confirmationMessage;

    // Scheduled
    private String cron;

    // Demand-based
    private int requiredRequests;
    private int requestExpirationSeconds;
}