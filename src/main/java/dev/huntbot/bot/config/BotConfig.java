package dev.huntbot.bot.config;

import dev.huntbot.bot.config.commands.CommandConfig;
import dev.huntbot.bot.config.components.ComponentConfig;
import dev.huntbot.bot.config.pings.PingConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link BotConfig BotConfig.java}
 *
 * Stores configurations for a bot instance.
 *
 * @copyright WeslayCodes & Contributors 2023
 */
@Getter
@Setter
@ToString
public class BotConfig {
    /**
     * The {@link MainConfig main configurations} for developers
     */
    private MainConfig mainConfig = new MainConfig();

    /**
     * Collection of {@link CommandConfig command configurations} the bot uses
     */
    private Map<String, CommandConfig> commandConfig = new HashMap<>();

    /**
     * {@link StringConfig String constants} the bot uses for responses and more
     */
    private StringConfig stringConfig = new StringConfig();

    /**
     * Non-intuitive number constants the bot uses
     */
    private ConstantConfig constantConfig = new ConstantConfig();

    /**
     * Configurations regarding roles and how they're pinged
     */
    private PingConfig pingConfig = new PingConfig();

    private ComponentConfig componentConfig = new ComponentConfig();
}
