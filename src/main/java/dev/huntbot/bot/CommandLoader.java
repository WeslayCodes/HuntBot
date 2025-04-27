package dev.huntbot.bot;

import dev.huntbot.HuntBotApp;
import dev.huntbot.api.util.Configured;
import dev.huntbot.bot.config.commands.CommandConfig;
import dev.huntbot.bot.config.commands.SubcommandConfig;
import dev.huntbot.commands.Subcommand;
import dev.huntbot.util.logging.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CommandLoader implements Configured {
    public static void deployCommands() {
        Log.info(CommandLoader.class, "Deploying commands...");

        Map<String, CommandConfig> commandData = CONFIG.getCommandConfig();

        List<SlashCommandData> guildCommands = new ArrayList<>();

        for (CommandConfig command : commandData.values()) {
            guildCommands.add(SlashCommandData.fromData(DataObject.fromJson(command.toString())));
        }

        Guild guild = HuntBotApp.getBot().getJDA().getGuildById(CONFIG.getMainConfig().getGuild());

        HuntBotApp.getBot().getJDA().updateCommands().addCommands().queue();

        if (guild != null) {
            guild.updateCommands().addCommands(guildCommands).queue(null, e -> {
                Log.error(CommandLoader.class, "Failed to deploy to development guild", e);
                System.exit(-1);
            });
        } else {
            Log.error(
                CommandLoader.class, "Unable to find guild. Could not deploy commands!", new IllegalArgumentException()
            );
            System.exit(-1);
        }

        Log.info(CommandLoader.class, "Commands successfully deployed");
    }

    public static void registerSubcommands() {
        Map<String, CommandConfig> commandData = CONFIG.getCommandConfig();

        for (CommandConfig commandVal : commandData.values()) {
            Map<String, SubcommandConfig> subcommandData = commandVal.getSubcommands();

            for (SubcommandConfig subcommandVal : subcommandData.values()) {
                String subcommandName = "/%s %s".formatted(commandVal.getName(), subcommandVal.getName());
                Log.debug(CommandLoader.class, "Registering subcommand %s...".formatted(subcommandName));

                try {
                    Class<? extends Subcommand> subcommandClass = Class.forName(subcommandVal.getLocation())
                        .asSubclass(Subcommand.class);
                    Constructor<? extends Subcommand> subcommandConstructor = subcommandClass
                        .getDeclaredConstructor(SlashCommandInteractionEvent.class);

                    HuntBotApp.getBot().getSubcommands()
                        .put(commandVal.getName() + subcommandVal.getName(), subcommandConstructor);
                } catch (ClassNotFoundException exception) {
                    Log.error(
                        CommandLoader.class, "Invalid class location for %s".formatted(subcommandName), exception
                    );
                    System.exit(-1);
                } catch (NoSuchMethodException exception) {
                    Log.error(
                        CommandLoader.class, "%s does not have a valid constructor".formatted(subcommandName), exception
                    );
                    System.exit(-1);
                }

                Log.debug(
                    CommandLoader.class,
                    "Subcommand /%s %s registered".formatted(commandVal.getName(), subcommandVal.getName())
                );
            }
        }
    }
}
