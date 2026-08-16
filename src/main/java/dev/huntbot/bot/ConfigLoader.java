package dev.huntbot.bot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.huntbot.HuntBotApp;
import dev.huntbot.bot.config.*;
import dev.huntbot.bot.config.commands.CommandConfig;
import dev.huntbot.bot.config.components.ComponentConfig;
import dev.huntbot.bot.config.pings.PingConfig;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.resource.ResourceUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

class ConfigLoader {
    private static final BotConfig config = HuntBotApp.getBot().getConfig();

    public static String basePath = "config/";
    public static String mainPath = basePath + "config.json";

    public static String langPath = basePath + "lang/";
    public static String strsPath = langPath + "en_us.json";

    public static String utilPath = basePath + "util/";
    public static String constantsPath = utilPath + "constants.json";
    public static String pingsPath = utilPath + "pings.json";

    public static String discordPath = basePath + "discord/";
    public static String cmdsPath = discordPath + "commands.json";
    public static String componentsPath = discordPath + "components.json";
    
    public static void loadConfig() {
        try {
            Log.debug(ConfigLoader.class, "Attempting to load config...");

            config.setMainConfig(getFromJson(mainPath, MainConfig.class));

            if (HuntBotApp.getEnv("GUILD_ID") != null) {
                config.getMainConfig().setGuild(HuntBotApp.getEnv("GUILD_ID"));
            }

            if (HuntBotApp.getEnv("LOG_CHANNEL") != null) {
                config.getMainConfig().setLogChannel(HuntBotApp.getEnv("LOG_CHANNEL"));
            }

            if (HuntBotApp.getEnv("PING_CHANNEL") != null) {
                config.getMainConfig().setPingChannel(HuntBotApp.getEnv("PING_CHANNEL"));
            }

            config.setStringConfig(getFromJson(strsPath, StringConfig.class));

            config.setConstantConfig(getFromJson(constantsPath, ConstantConfig.class));

            config.setPingConfig(getFromJson(pingsPath, PingConfig.class));

            if (HuntBotApp.getEnv("CATACLYSMIC_ID") != null) {
                config.getPingConfig().getManual().get("cataclysmic").setRoleId(
                    new Gson().fromJson(HuntBotApp.getEnv("CATACLYSMIC_ID"), String.class)
                );
            }

            if (HuntBotApp.getEnv("PLHLEGBLAST_ID") != null) {
                config.getPingConfig().getManual().get("plhlegblast").setRoleId(
                    new Gson().fromJson(HuntBotApp.getEnv("PLHLEGBLAST_ID"), String.class)
                );
            }

            if (HuntBotApp.getEnv("GOLDEN_BALL_ID") != null) {
                config.getPingConfig().getManual().get("goldenBall").setRoleId(
                    new Gson().fromJson(HuntBotApp.getEnv("GOLDEN_BALL_ID"), String.class)
                );
            }

            if (HuntBotApp.getEnv("CHESTPLATE_ID") != null) {
                config.getPingConfig().getDemandBased().get("chestplate").setRoleId(
                    new Gson().fromJson(HuntBotApp.getEnv("CHESTPLATE_ID"), String.class)
                );
            }

            config.setCommandConfig(getFromJson(cmdsPath, new TypeToken<Map<String, CommandConfig>>(){}.getType()));

            config.setComponentConfig(getFromJson(componentsPath, ComponentConfig.class));

            Log.debug(ConfigLoader.class, "Successfully loaded config");
        } catch (IOException exception) {
            Log.error(ConfigLoader.class, "Unable to read one or more config files", exception);
            System.exit(-1);
        }
    }

    private static <T> T getFromJson(String path, Class<T> clazz) throws IOException {
        try (InputStream stream = ResourceUtil.getResourceStream(path)) {
            if (stream == null) {
                throw new IOException("Could not find resource " + path);
            }

            InputStreamReader reader = new InputStreamReader(stream);
            return new Gson().fromJson(reader, clazz);
        }
    }

    private static <T> T getFromJson(String path, Type type) throws IOException {
        try (InputStream stream = ResourceUtil.getResourceStream(path)) {
            if (stream == null) {
                throw new IOException("Could not find resource " + path);
            }

            InputStreamReader reader = new InputStreamReader(stream);
            return new Gson().fromJson(reader, type);
        }
    }
}
