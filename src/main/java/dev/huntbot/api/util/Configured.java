package dev.huntbot.api.util;

import dev.huntbot.HuntBotApp;
import dev.huntbot.bot.config.*;

public interface Configured {
    BotConfig CONFIG = HuntBotApp.getBot().getConfig();
    NumberConfig NUMS = CONFIG.getNumberConfig();
    StringConfig STRS = CONFIG.getStringConfig();
}
