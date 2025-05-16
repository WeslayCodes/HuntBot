package dev.huntbot.bot.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MainConfig {
    private String[] devs = {};

    /**
     * The guild ID the bot uses to deploy commands
     */
    private String guild = "";

    /**
     * The text channel ID the bot sends certain logs to
     */
    private String logChannel = "";

    private String pingChannel = "";

    private String uploadChannel = "";

    private String hunterChannel = "";

    private String huntForumChannel = "";

    private String[] threads = {};

    private String[] roles = {};
}
