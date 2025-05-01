package dev.huntbot.bot.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link StringConfig StringConfig.java}
 *
 * Stores string configurations for a bot instance.
 *
 * @copyright WeslayCodes & Contributors 2023
 */
@Getter
@Setter
@ToString
public class StringConfig {
    private String activityStatus = "";
    private String onCooldown = "";
    private String error = "";
    private String loremasterWaiting = "";
    private String loremasterActive = "";
    private String helmetAmount = "";
    private String sailorEmoticon = "";
    private String sailorAdded = "";
    private String sailorRemoved = "";
    private String sailorFailed = "";
    private String lfgPing = "";
    private String lfgSuccess = "";
    private String lfgThreadMsg = "";
    private String uuidEndpoint = "";
    private String profilesEndpoint = "";
    private String autocompleteBadIgn = "";
    private String autocompleteNoProfiles = "";
    private String progressUserInvalid = "";
    private String progressNotCached = "";
    private String progressInvalidProfile = "";
    private String progressEmojiYes = "";
    private String progressEmojiNo = "";
    private String progressHeader = "";
    private String progressBookshelf = "";
    private String progressLoremaster = "";
    private String progressBlessed = "";
    private String progressBlessedExtra = "";
    private String progressSailor = "";
    private String progressNapkin = "";
    private String youtubeEndpoint = "";
    private String geminiEndpoint = "";
    private String geminiRequest = "";
    private String geminiInputText = "";
}
