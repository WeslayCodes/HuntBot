package dev.huntbot.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.huntbot.api.util.Configured;
import dev.huntbot.util.api.ApiRequest;
import dev.huntbot.util.hunt.ChoiceRecord;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.time.TimeUtil;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AutofillListener extends ListenerAdapter implements Runnable, Configured {
    public static Map<String, String> userUuids = new ConcurrentHashMap<>();
    public static Map<String, ChoiceRecord> userProfiles = new ConcurrentHashMap<>();
    private CommandAutoCompleteInteractionEvent event = null;

    public AutofillListener() {
        super();
    }

    public AutofillListener(CommandAutoCompleteInteractionEvent event) {
        this.event = event;
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        new Thread(new AutofillListener(event)).start();
    }

    @Override
    public void run() {
        OptionMapping ignRaw = this.event.getOption("ign");

        if (ignRaw == null) {
            return;
        }

        String ign = ignRaw.getAsString().replaceAll("[^A-Za-z0-9_]", "");
        ign = ign.substring(0, Math.min(ign.length(), 16));

        boolean isInCache = AutofillListener.userProfiles.containsKey(ign);

        if (isInCache && AutofillListener.userProfiles.get(ign).choices().isEmpty()) {
            this.event.replyChoice(STRS.getAutocompleteNoProfiles().formatted(ign), "N/A").queue();
            return;
        }

        if (isInCache) {
            this.event.replyChoices(AutofillListener.userProfiles.get(ign).choices()).queue();
            return;
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            String uuid = this.getUuid(ign, client);

            if (uuid == null) {
                this.event.replyChoice(STRS.getAutocompleteBadIgn().formatted(ign), "N/A").queue();
                return;
            }

            List<Command.Choice> choices = this.getProfileChoices(uuid, ign, client);

            if (choices.isEmpty()) {
                this.event.replyChoice(STRS.getAutocompleteNoProfiles().formatted(ign), -2).queue();
                return;
            }

            this.event.replyChoices(choices).queue();
        } catch (IOException exception) {
            Log.error(this.getClass(), "Failed to send/receive information", exception);
        } catch (InterruptedException exception) {
            Log.error(this.getClass(), "Send/receive was interrupted", exception);
        }
    }

    private String getUuid(String ign, HttpClient client) throws IOException, InterruptedException {
        JsonObject json = ApiRequest.getPlayerUuidData(ign, client);

        if (json.get("id") == null) {
            return null;
        }

        AutofillListener.userUuids.put(ign, json.get("id").getAsString());
        return json.get("id").getAsString();
    }

    private List<Command.Choice> getProfileChoices(String uuid, String ign, HttpClient client)
        throws IOException, InterruptedException {
        JsonObject json = ApiRequest.getPlayerProfilesData(uuid, client);
        List<Command.Choice> choices = new ArrayList<>();

        JsonElement profileArrayElement = json.get("profiles");

        if (profileArrayElement == null || profileArrayElement.isJsonNull()) {
            AutofillListener.userProfiles.put(ign, new ChoiceRecord(choices, TimeUtil.getCurMilli()));
            return choices;
        }

        JsonArray profileArray = profileArrayElement.getAsJsonArray();

        for (JsonElement profileElement : profileArray) {
            choices.add(getChoice(profileElement));
        }

        AutofillListener.userProfiles.put(ign, new ChoiceRecord(choices, TimeUtil.getCurMilli()));

        return choices;
    }

    private static Command.Choice getChoice(JsonElement profileElement) {
        JsonObject profileData = profileElement.getAsJsonObject();
        String fruitName = profileData.get("cute_name").getAsString();
        String profileName = fruitName;
        JsonElement profileTypeElement = profileData.get("game_mode");

        if (profileTypeElement != null) {
            profileName = profileName + switch (profileTypeElement.getAsString()) {
                case "ironman" -> " - Ironman";
                case "island" -> " - Stranded";
                case "bingo" -> " - Bingo";
                default -> "";
            };
        }

        return new Command.Choice(profileName, fruitName);
    }
}
