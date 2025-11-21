package data_access.outfit_suggestion;

import data_access.weather.WeatherAPIConfig;
import entity.User;
import entity.DailyForecast;
import entity.ForecastSlot;
import use_case.outfit_suggestion.OutfitSuggestionDataAccessInterface;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// handles calling the weather api and gemini ai for outfit suggestions
public class OutfitSuggestionDataAccessObject implements OutfitSuggestionDataAccessInterface {

    private final OkHttpClient client;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    // api urls
    private static final String WEATHER_API_BASE = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String GEMINI_API_BASE = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash-lite:generateContent";
//    private static final String GEMINI_API_BASE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public OutfitSuggestionDataAccessObject() {
        this.client = new OkHttpClient();
    }

    @Override
    public DailyForecast getWeatherForecast(String location) {
        try {
            // build url with location and api key
            String url = WEATHER_API_BASE +
                    "?q=" + location +
                    "&appid=" + WeatherAPIConfig.API_KEY +
                    "&units=metric" +
                    "&cnt=8";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("Weather API failed: " + response.code());
                    return null;
                }

                if (response.body() == null) {
                    System.err.println("Weather API returned empty body");
                    return null;
                }

                String responseBody = response.body().string();
                return parseWeatherResponse(responseBody, location);
            }

        } catch (Exception e) {
            System.err.println("Error fetching weather: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> generateOutfitSuggestions(User user, DailyForecast forecast) {
        try {
            // create the prompt for ai
            String prompt = buildPrompt(user, forecast);

            // make it into json
            String jsonBody = buildGeminiRequest(prompt);

            // call gemini api
            String url = GEMINI_API_BASE + "?key=" + GeminiConfig.API_KEY;

            RequestBody body = RequestBody.create(jsonBody, JSON_MEDIA_TYPE);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("Gemini API failed: " + response.code());
                    if (response.body() != null) {
                        System.err.println("Response: " + response.body().string());
                    }
                    return null;
                }

                if (response.body() == null) {
                    System.err.println("Gemini API returned empty body");
                    return null;
                }

                String responseBody = response.body().string();
                return parseGeminiResponse(responseBody);
            }

        } catch (Exception e) {
            System.err.println("Error generating outfit suggestions: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // turn weather json into our DailyForecast object
    private DailyForecast parseWeatherResponse(String responseBody, String city) {
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray list = json.getJSONArray("list");

            List<ForecastSlot> slots = new ArrayList<>();

            // grab first 4 time slots (next 12 hours)
            for (int i = 0; i < Math.min(4, list.length()); i++) {
                JSONObject item = list.getJSONObject(i);

                // get temperature stuff
                JSONObject main = item.getJSONObject("main");
                double temp = main.getDouble("temp");
                double feelsLike = main.optDouble("feels_like", temp);

                // get weather description
                JSONArray weather = item.getJSONArray("weather");
                String description = weather.getJSONObject(0).getString("description");
                String iconCode = weather.getJSONObject(0).getString("icon");

                // get wind
                JSONObject wind = item.optJSONObject("wind");
                double windSpeed = wind != null ? wind.optDouble("speed", 0.0) : 0.0;

                // chance of rain
                double precipProb = item.optDouble("pop", 0.0);

                // label it like "now" or "in 3 hours"
                String timeLabel = getTimeLabel(i);

                ForecastSlot slot = new ForecastSlot(
                        timeLabel,
                        temp,
                        description,
                        iconCode,
                        precipProb,
                        windSpeed,
                        feelsLike
                );

                slots.add(slot);
            }

            return new DailyForecast(city, LocalDate.now(), slots);

        } catch (Exception e) {
            System.err.println("Error parsing weather response: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // build the prompt we'll send to gemini
    private String buildPrompt(User user, DailyForecast forecast) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a personal fashion stylist. Generate 3 outfit suggestions based on:\n\n");

        // user info
        prompt.append("User Profile:\n");
        prompt.append("- Gender: ").append(user.getGender()).append("\n");
        prompt.append("- Location: ").append(user.getLocation()).append("\n");

        // what clothes they like
        prompt.append("\nClothing Preferences (items they own/like):\n");
        Map<String, Boolean> style = user.getStyle();
        if (style != null && !style.isEmpty()) {
            for (Map.Entry<String, Boolean> entry : style.entrySet()) {
                if (entry.getValue()) {
                    prompt.append("- ").append(entry.getKey()).append("\n");
                }
            }
        }

        // current weather
        prompt.append("\nWeather Conditions in ").append(forecast.getCity()).append(":\n");
        if (!forecast.getSlots().isEmpty()) {
            ForecastSlot firstSlot = forecast.getSlots().get(0);
            prompt.append("- Temperature: ").append(String.format("%.1f", firstSlot.getTemperature())).append("°C\n");
            prompt.append("- Feels like: ").append(String.format("%.1f", firstSlot.getFeelsLike())).append("°C\n");
            prompt.append("- Conditions: ").append(firstSlot.getDescription()).append("\n");
            prompt.append("- Wind: ").append(String.format("%.1f", firstSlot.getWindSpeed())).append(" m/s\n");
            if (firstSlot.getPrecipProbability() != null && firstSlot.getPrecipProbability() > 0) {
                prompt.append("- Precipitation chance: ").append(String.format("%.0f", firstSlot.getPrecipProbability() * 100)).append("%\n");
            }
        }

        // tell ai what to do
        prompt.append("\nProvide 3 different outfit suggestions. For each outfit:\n");
        prompt.append("1. List specific clothing items from their preferences\n");
        prompt.append("2. Explain why it's suitable for the weather\n");
        prompt.append("3. Keep it practical and comfortable\n\n");
        prompt.append("Format each outfit as:\nOutfit [number]: [clothing items]\nWhy: [brief explanation]\n");

        return prompt.toString();
    }

    // format prompt into gemini's expected json structure
    private String buildGeminiRequest(String prompt) {
        JSONObject request = new JSONObject();

        JSONArray contents = new JSONArray();
        JSONObject content = new JSONObject();

        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", prompt);
        parts.put(part);

        content.put("parts", parts);
        contents.put(content);

        request.put("contents", contents);

        return request.toString();
    }

    // pull the outfit suggestions out of gemini's response
    private List<String> parseGeminiResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);

            JSONArray candidates = json.getJSONArray("candidates");
            if (candidates.length() == 0) {
                return null;
            }

            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject content = firstCandidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");

            if (parts.length() == 0) {
                return null;
            }

            String text = parts.getJSONObject(0).getString("text");

            // split into separate outfits
            List<String> suggestions = new ArrayList<>();
            String[] lines = text.split("\n\n");

            StringBuilder currentOutfit = new StringBuilder();
            for (String line : lines) {
                if (line.trim().startsWith("Outfit ") && currentOutfit.length() > 0) {
                    suggestions.add(currentOutfit.toString().trim());
                    currentOutfit = new StringBuilder();
                }
                currentOutfit.append(line).append("\n");
            }

            if (currentOutfit.length() > 0) {
                suggestions.add(currentOutfit.toString().trim());
            }

            return suggestions.isEmpty() ? List.of(text) : suggestions;

        } catch (Exception e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // give each time slot a label
    private String getTimeLabel(int slotIndex) {
        switch (slotIndex) {
            case 0: return "Now";
            case 1: return "In 3 hours";
            case 2: return "In 6 hours";
            case 3: return "In 9 hours";
            default: return "Later";
        }
    }
}