package dev.huntbot.bot.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link ConstantConfig ConstantConfig.java}
 *
 * Stores number configurations for a bot instance.
 *
 * @copyright WeslayCodes & Contributors 2023
 */
@Getter
@Setter
@ToString
public class ConstantConfig {
    private int[] manualPingLockMaxDelaySeconds = {};
    private String[] autoPingCronStrs = {};
    private int[] autoPingLockMaxDelaySeconds = {};
    private int interactiveIdle = 0;
    private int interactiveHardStop = 0;
    private int threadInteractiveStop;
}
