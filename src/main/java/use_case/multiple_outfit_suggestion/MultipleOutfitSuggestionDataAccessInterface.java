package use_case.multiple_outfit_suggestion;

import entity.User;
import entity.DailyForecast;
import java.util.List;

/**
 * Data Access Interface for Multiple Outfit Suggestion Use Case.
 * Note: User data access is handled by UserRepository interface.
 * This interface only handles weather and AI-related data access.
 */
public interface MultipleOutfitSuggestionDataAccessInterface {

    /**
     * Gets the daily weather forecast for a location.
     * @param location the city/location to get weather for
     * @return the DailyForecast object, or null if not found
     */
    DailyForecast getWeatherForecast(String location);

    /**
     * Generates multiple outfit suggestions using an LLM based on user preferences and weather.
     * @param user the user with style preferences
     * @param forecast the daily forecast with weather conditions
     * @param numberOfSuggestions the number of different suggestions to generate
     * @return a list of outfit suggestion strings
     */
    List<String> generateMultipleOutfitSuggestions(User user, DailyForecast forecast, int numberOfSuggestions);
}
