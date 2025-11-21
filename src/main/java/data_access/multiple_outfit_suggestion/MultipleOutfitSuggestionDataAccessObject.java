package data_access.multiple_outfit_suggestion;

import data_access.weather.WeatherAPIConfig;
import data_access.outfit_suggestion.GeminiConfig;
import entity.User;
import entity.DailyForecast;
import entity.ForecastSlot;
import use_case.multiple_outfit_suggestion.MultipleOutfitSuggestionDataAccessInterface;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Multiple Outfit Suggestion Use Case.
 * Handles calling the Weather API and Gemini AI for generating multiple outfit suggestions.
 */
public class MultipleOutfitSuggestionDataAccessObject implements MultipleOutfitSuggestionDataAccessInterface {

    private final OkHttpClient client;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    // API URLs
    private static final String WEATHER_API_BASE = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String GEMINI_API_BASE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";

    public MultipleOutfitSuggestionDataAccessObject() {
        this.client = new OkHttpClient();
    }

    @Override
    public DailyForecast getWeatherForecast(String location) {
        try {
            // Build URL with location and API key
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
    public List<String> generateMultipleOutfitSuggestions(User user, DailyForecast forecast, int numberOfSuggestions) {
        try {
            // Create the prompt for AI with variable number of suggestions
            String prompt = buildPrompt(user, forecast, numberOfSuggestions);

            // Convert to JSON
            String jsonBody = buildGeminiRequest(prompt);

            // Call Gemini API
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

    /**
     * Parse weather JSON response into DailyForecast object.
     */
    private DailyForecast parseWeatherResponse(String responseBody, String city) {
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray list = json.getJSONArray("list");

            List<ForecastSlot> slots = new ArrayList<>();

            // Get first 4 time slots (next 12 hours)
            for (int i = 0; i < Math.min(4, list.length()); i++) {
                JSONObject item = list.getJSONObject(i);

                // Get temperature data
                JSONObject main = item.getJSONObject("main");
                double temp = main.getDouble("temp");
                double feelsLike = main.optDouble("feels_like", temp);

                // Get weather description
                JSONArray weather = item.getJSONArray("weather");
                String description = weather.getJSONObject(0).getString("description");
                String iconCode = weather.getJSONObject(0).getString("icon");

                // Get wind
                JSONObject wind = item.optJSONObject("wind");
                double windSpeed = wind != null ? wind.optDouble("speed", 0.0) : 0.0;

                // Chance of rain
                double precipProb = item.optDouble("pop", 0.0);

                // Label it like "now" or "in 3 hours"
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

    /**
     * Build the prompt to send to Gemini AI for multiple outfit suggestions.
     */
    private String buildPrompt(User user, DailyForecast forecast, int numberOfSuggestions) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a personal fashion stylist. Generate ")
              .append(numberOfSuggestions)
              .append(" DIFFERENT outfit suggestions based on:\n\n");

        // User info
        prompt.append("User Profile:\n");
        prompt.append("- Gender: ").append(user.getGender()).append("\n");
        prompt.append("- Location: ").append(user.getLocation()).append("\n");

        // Clothing preferences
        prompt.append("\nClothing Preferences (items they own/like):\n");
        Map<String, Boolean> style = user.getStyle();
        if (style != null && !style.isEmpty()) {
            for (Map.Entry<String, Boolean> entry : style.entrySet()) {
                if (entry.getValue()) {
                    prompt.append("- ").append(entry.getKey()).append("\n");
                }
            }
        }

        // Current weather
        prompt.append("\nWeather Conditions in ").append(forecast.getCity()).append(":\n");
        if (!forecast.getSlots().isEmpty()) {
            ForecastSlot firstSlot = forecast.getSlots().get(0);
            prompt.append("- Temperature: ").append(String.format("%.1f", firstSlot.getTemperature())).append("°C\n");
            prompt.append("- Feels like: ").append(String.format("%.1f", firstSlot.getFeelsLike())).append("°C\n");
            prompt.append("- Conditions: ").append(firstSlot.getDescription()).append("\n");
            prompt.append("- Wind: ").append(String.format("%.1f", firstSlot.getWindSpeed())).append(" m/s\n");
            if (firstSlot.getPrecipProbability() != null && firstSlot.getPrecipProbability() > 0) {
                prompt.append("- Precipitation chance: ")
                      .append(String.format("%.0f", firstSlot.getPrecipProbability() * 100))
                      .append("%\n");
            }
        }

        // Instructions for AI
        prompt.append("\nProvide ").append(numberOfSuggestions)
              .append(" DIFFERENT outfit suggestions. Each outfit should be DISTINCT and offer variety ");
        prompt.append("in style, formality, or occasion. For each outfit:\n");
        prompt.append("1. List specific clothing items from their preferences\n");
        prompt.append("2. Explain why it's suitable for the weather\n");
        prompt.append("3. Keep it practical and comfortable\n");
        prompt.append("4. Make sure each suggestion is notably different from the others\n\n");
        prompt.append("Format each outfit as:\nOutfit [number]: [clothing items]\nWhy: [brief explanation]\n");

        return prompt.toString();
    }

    /**
     * Format prompt into Gemini's expected JSON structure.
     */
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

    /**
     * Parse outfit suggestions from Gemini's response.
     */
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

            // Split into separate outfits
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

    /**
     * Generate time labels for forecast slots.
     */
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
