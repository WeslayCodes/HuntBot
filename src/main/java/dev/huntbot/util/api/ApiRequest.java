package dev.huntbot.util.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.huntbot.HuntBotApp;
import dev.huntbot.api.util.Configured;
import dev.huntbot.util.logging.Log;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiRequest implements Configured {
    public static JsonObject getPlayerUuidData(String ign, HttpClient client) throws IOException, InterruptedException {
        String uri = STRS.getUuidEndpoint().formatted(ign);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Log.info(ApiRequest.class, "Hit Mojang API for " + ign);

        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public static JsonObject getPlayerProfilesData(String uuid, HttpClient client)
        throws IOException, InterruptedException {
        String uri = STRS.getProfilesEndpoint().formatted(HuntBotApp.getEnv("API_KEY"), uuid);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Log.info(ApiRequest.class, "Hit Hypixel API for " + uuid);

        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public static JsonObject getLastVideo(HttpClient client) throws IOException, InterruptedException {
        String uri = STRS.getYoutubeEndpoint().formatted(HuntBotApp.getEnv("GOOGLE_API_KEY"));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Log.info(ApiRequest.class, "Hit YouTube API");

        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public static JsonObject getGeneratedString(String prompt, HttpClient client)
        throws IOException, InterruptedException
    {
        prompt = STRS.getGeminiRules() + prompt;
        String uri = STRS.getGeminiEndpoint().formatted(HuntBotApp.getEnv("GOOGLE_API_KEY"));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .POST(HttpRequest.BodyPublishers.ofString(STRS.getGeminiRequest().formatted(prompt)))
            .header("Content-Type", "application/json")
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Log.info(ApiRequest.class, "Hit Gemini API");

        return JsonParser.parseString(response.body()).getAsJsonObject();
    }
}
