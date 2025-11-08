package dev.huntbot.api.bot;

import dev.huntbot.bot.config.BotConfig;
import dev.huntbot.commands.Subcommand;
import dev.huntbot.interactive.Interactive;
import net.dv8tion.jda.api.JDA;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public interface Bot {
    void create();
    JDA getJDA();
    BotConfig getConfig();
    void deployCommands();
    Map<String, Constructor<? extends Subcommand>> getSubcommands();
    ConcurrentMap<String, Interactive> getInteractives();
}