package dev.huntbot.bot.config.commands;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link SubcommandArgsConfig SubcommandArgsConfig.java}
 *
 * Stores subcommand argument configurations for a bot
 * instance.
 *
 * @copyright WeslayCodes & Contributors 2023
 */
@Getter
@Setter
public class SubcommandArgsConfig {
    private String name = "";
    private int type = -1;
    private String description = "";
    private Boolean required = false;
    private Boolean autocomplete = false;
    private Integer max_value = Integer.MAX_VALUE;
    private Integer max_length = 100;
    private ArgChoicesConfig<?>[] choices;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(
            "{\"name\":\"%s\",\"type\":%d,\"description\":\"%s\""
                .formatted(name, type, description)
        );

        if (required) {
            sb.append(",\"required\":%b".formatted(true));
        }

        if (autocomplete) {
            sb.append(",\"autocomplete\":%b".formatted(true));
        }

        sb.append(",\"max_value\":%d".formatted(max_value));

        sb.append(",\"max_length\":%d".formatted(max_length));

        if (choices != null) {
            for (int i=0; i<choices.length; i++) {
                ArgChoicesConfig<?> choice = choices[i];

                if (i == 0) {
                    sb.append(",\"choices\":[");
                }

                sb.append("%s".formatted(choice.toString()));

                if (i == choices.length-1) {
                    sb.append("]");
                    continue;
                }

                sb.append(",");
            }
        }

        sb.append("}");

        return sb.toString();
    }
}
