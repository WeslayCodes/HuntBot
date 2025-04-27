package dev.huntbot;

import dev.huntbot.api.bot.Bot;
import dev.huntbot.bot.HuntBot;
import dev.huntbot.bot.EnvironmentType;
import dev.huntbot.util.interaction.InteractionUtil;
import dev.huntbot.util.logging.Log;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

import java.nio.file.Paths;

/**
 * {@link HuntBotApp BoarBotApp.java}
 *
 * Creates the bot instance using CLI args.
 *
 * @copyright WeslayCodes & Contributors 2023
 */
public class HuntBotApp {
    @Getter private static Bot bot;
    private static final Dotenv env = Paths.get(".env").toFile().exists()
        ? Dotenv.configure()
            .filename(".env")
            .load()
        : null;
    private static EnvironmentType environmentType;

    public static void main(String... args) {
        if (args.length > 0) {
            HuntBotApp.environmentType = switch (args[0]) {
                case "test" -> EnvironmentType.TEST;
                case "prod" -> EnvironmentType.PROD;
                default -> EnvironmentType.DEV;
            };
        } else {
            HuntBotApp.environmentType = EnvironmentType.DEV;
        }

        bot = new HuntBot();
        bot.create();

        try {
            bot.getJDA().awaitReady();
        } catch (InterruptedException exception) {
            Log.error(HuntBotApp.class, "Main thread interrupted before bot was ready", exception);
            System.exit(-1);
        }

        if (environmentType == EnvironmentType.PROD) {
            bot.deployCommands();
        }

        if (environmentType == EnvironmentType.PROD || args.length > 1 && Boolean.parseBoolean(args[1])) {
            bot.deployCommands();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(HuntBotApp::cleanup));
    }

    private static void cleanup() {
        InteractionUtil.shutdownScheduler();
    }

    public static void reset() {
        bot = null;
        main();
    }

    public static String getEnv(String key) {
        if (HuntBotApp.env == null) {
            return System.getenv(key);
        }

        return HuntBotApp.env.get(key);
    }

    public static EnvironmentType getEnvironmentType() { return HuntBotApp.environmentType; }
}
