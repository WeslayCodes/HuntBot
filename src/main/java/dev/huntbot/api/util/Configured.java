package dev.huntbot.api.util;

import dev.huntbot.HuntBotApp;
import dev.huntbot.bot.config.*;

public interface Configured {
    BotConfig CONFIG = HuntBotApp.getBot().getConfig();
    ConstantConfig CONSTANTS = CONFIG.getConstantConfig();
    StringConfig STRS = CONFIG.getStringConfig();
}
