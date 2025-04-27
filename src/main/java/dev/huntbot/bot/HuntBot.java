package dev.huntbot.bot;

import dev.huntbot.HuntBotApp;
import dev.huntbot.api.bot.Bot;
import dev.huntbot.api.util.Configured;
import dev.huntbot.bot.config.*;
import dev.huntbot.commands.Subcommand;
import dev.huntbot.listeners.*;
import dev.huntbot.util.logging.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HuntBot implements Bot, Configured {
    private JDA jda;

    private final BotConfig config = new BotConfig();

    private final Map<String, Constructor<? extends Subcommand>> subcommands = new ConcurrentHashMap<>();

    @Override
    public void create() {
        Log.info(this.getClass(), "Starting up bot...");

        ConfigLoader.loadConfig();
        CommandLoader.registerSubcommands();

        this.jda = JDABuilder.createDefault(HuntBotApp.getEnv("TOKEN"))
            .addEventListeners(new CommandListener(), new ReadyListener(), new AutofillListener())
            .setActivity(Activity.customStatus(STRS.getActivityStatus()))
            .build();
    }

    @Override
    public JDA getJDA() {
        return this.jda;
    }

    @Override
    public BotConfig getConfig() {
        return this.config;
    }

    @Override
    public void deployCommands() {
        CommandLoader.deployCommands();
    }

    @Override
    public Map<String, Constructor<? extends Subcommand>> getSubcommands() {
        return this.subcommands;
    }
}
