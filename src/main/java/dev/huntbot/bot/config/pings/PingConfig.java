package dev.huntbot.bot.config.pings;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class PingConfig {
    private Map<String, IndivPingConfig> manual = new HashMap<>();
    private Map<String, IndivPingConfig> scheduled = new HashMap<>();
    private Map<String, IndivPingConfig> demandBased = new HashMap<>();
}
