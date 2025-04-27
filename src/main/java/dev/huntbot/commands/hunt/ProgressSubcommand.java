package dev.huntbot.commands.hunt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.huntbot.commands.Subcommand;
import dev.huntbot.listeners.AutofillListener;
import dev.huntbot.util.api.ApiRequest;
import dev.huntbot.util.hunt.ProgressRecord;
import dev.huntbot.util.logging.ExceptionHandler;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.time.TimeUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Objects;

public class ProgressSubcommand extends Subcommand {
    public ProgressSubcommand(SlashCommandInteractionEvent event) {
        super(event);
    }

    @Override
    public void execute() {
        String ign = Objects.requireNonNull(this.event.getOption("ign")).getAsString();
        String profileName = Objects.requireNonNull(this.event.getOption("profile")).getAsString();

        boolean userInvalid = !AutofillListener.userUuids.containsKey(ign);

        if (userInvalid) {
            String userInvalidStr = STRS.getProgressUserInvalid().formatted(ign);
            this.event.getInteraction().reply(userInvalidStr).setEphemeral(true).queue(null,
                e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
            );
            return;
        }

        boolean profilesUncached = !AutofillListener.userProfiles.containsKey(ign);

        if (profilesUncached) {
            this.event.getInteraction().reply(STRS.getProgressNotCached()).setEphemeral(true).queue(null,
                e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
            );
            return;
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            ProgressRecord progressData;
            
            try {
                progressData = this.getProgressData(ign, profileName, client);
            } catch (NullPointerException exception) {
                progressData = new ProgressRecord();
            } catch (IllegalArgumentException | IllegalStateException exception) {
                String invalidProfileStr = STRS.getProgressInvalidProfile().formatted(ign, profileName);
                this.event.getInteraction().reply(invalidProfileStr).setEphemeral(true).queue(null,
                    e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
                );
                return;
            }

            String progressStr = getProgressString(ign, profileName, progressData);

            this.event.getInteraction().reply(progressStr).setEphemeral(true).queue(null,
                e -> ExceptionHandler.replyHandle(this.event, this.getClass(), e)
            );

            Log.debug(this.user, this.getClass(), "Sent progress data: " + progressData);
        } catch (IOException exception) {
            Log.error(this.getClass(), "Failed to send/receive information", exception);
        } catch (InterruptedException exception) {
            Log.error(this.getClass(), "Send/receive was interrupted", exception);
        }
    }

    private ProgressRecord getProgressData(String ign, String profileName, HttpClient client)
        throws IOException, InterruptedException, NullPointerException {
        String uuid = AutofillListener.userUuids.get(ign);
        JsonObject json = ApiRequest.getPlayerProfilesData(uuid, client);
        
        JsonElement profileArrayElement = json.get("profiles");

        if (profileArrayElement == null || profileArrayElement.isJsonNull()) {
            throw new IllegalStateException();
        }

        JsonArray profileArray = profileArrayElement.getAsJsonArray();

        for (JsonElement profileElement : profileArray) {
            JsonObject profileData = profileElement.getAsJsonObject();
            String curProfileName = profileData.get("cute_name").getAsString();

            if (!curProfileName.equals(profileName)) {
                continue;
            }

            JsonObject userQuestData = profileData.getAsJsonObject("members").getAsJsonObject(uuid)
                .getAsJsonObject("nether_island_player_data").getAsJsonObject("quests");

            return new ProgressRecord(
                userQuestData.get("found_kuudra_book") != null,
                userQuestData.get("kuudra_loremaster") != null,
                userQuestData.get("last_believer_blessing") == null
                    ? TimeUtil.getCurMilli() - TimeUtil.getThirtyMinMilli()
                    : userQuestData.get("last_believer_blessing").getAsLong(),
                userQuestData.get("weird_sailor") != null,
                userQuestData.get("fished_wet_napkin") != null
            );
        }

        throw new IllegalArgumentException();
    }

    private static String getProgressString(String ign, String profileName, ProgressRecord progressData) {
        String progressStr = STRS.getProgressHeader().formatted(ign, profileName);

        progressStr = progressStr + STRS.getProgressBookshelf()
            .formatted(progressData.bookshelf() ? STRS.getProgressEmojiYes() : STRS.getProgressEmojiNo());
        progressStr = progressStr + STRS.getProgressLoremaster()
            .formatted(progressData.bookshelf() ? STRS.getProgressEmojiYes() : STRS.getProgressEmojiNo());

        long thirtyMinsAgo = TimeUtil.getCurMilli() - TimeUtil.getThirtyMinMilli();
        long blessEnd = (progressData.lastBlessed() + TimeUtil.getThirtyMinMilli()) / 1000;
        boolean shouldShowExtra = progressData.lastBlessed() > thirtyMinsAgo;

        progressStr = progressStr + STRS.getProgressBlessed().formatted(
            shouldShowExtra ? STRS.getProgressEmojiYes() : STRS.getProgressEmojiNo(),
            shouldShowExtra ? STRS.getProgressBlessedExtra().formatted(blessEnd) : ""
        );

        progressStr = progressStr + STRS.getProgressSailor()
            .formatted(progressData.sailor() ? STRS.getProgressEmojiYes() : STRS.getProgressEmojiNo());
        progressStr = progressStr + STRS.getProgressNapkin()
            .formatted(progressData.napkin() ? STRS.getProgressEmojiYes() : STRS.getProgressEmojiNo());

        return progressStr;
    }
}
