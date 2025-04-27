package dev.huntbot.util.hunt;

import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public record ChoiceRecord(List<Command.Choice> choices, long timestamp) {}
