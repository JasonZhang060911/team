package use_case.outfit_suggestion;

import entity.User;
import entity.DailyForecast;
import entity.ForecastSlot;
import java.util.List;

/**
 * The Outfit Suggestion Interactor.
 * This contains the business logic for generating personalized outfit suggestions
 * based on user preferences and current weather conditions.
 */
public class OutfitSuggestionInteractor implements OutfitSuggestionInputBoundary {

    private final User currentUser;
    private final OutfitSuggestionDataAccessInterface weatherAndAIAccess;
    private final OutfitSuggestionOutputBoundary presenter;

    /**
     * Constructor for OutfitSuggestionInteractor.
     * @param currentUser the logged in user with their preferences
     * @param weatherAndAIAccess the data access for weather and AI suggestions
     * @param presenter the presenter for showing results to the user
     */
    public OutfitSuggestionInteractor(
            User currentUser,
            OutfitSuggestionDataAccessInterface weatherAndAIAccess,
            OutfitSuggestionOutputBoundary presenter) {
        this.currentUser = currentUser;
        this.weatherAndAIAccess = weatherAndAIAccess;
        this.presenter = presenter;
    }

    /**
     * Executes the outfit suggestion use case.
     * @param inputData contains the username and location
     */
    @Override
    public void execute(OutfitSuggestionInputData inputData) {
        try {
            // Step 1: Use the already-logged-in user (no database query needed!)
            // The user object is passed in the constructor with all their preferences already loaded

            // Step 2: Get today's weather forecast for the location
            DailyForecast forecast = weatherAndAIAccess.getWeatherForecast(inputData.getLocation());

            if (forecast == null || forecast.getSlots().isEmpty()) {
                presenter.prepareFailView("Could not retrieve weather data for " + inputData.getLocation());
                return;
            }

            // Step 3: Generate outfit suggestions using the LLM
            // This combines user preferences (style, gender, etc.) with weather data
            List<String> outfitSuggestions = weatherAndAIAccess.generateOutfitSuggestions(currentUser, forecast);

            if (outfitSuggestions == null || outfitSuggestions.isEmpty()) {
                presenter.prepareFailView("Could not generate outfit suggestions. Please try again.");
                return;
            }

            // Step 4: Get temperature info for display (from first slot)
            ForecastSlot firstSlot = forecast.getSlots().get(0);
            double currentTemp = firstSlot.getTemperature();

            // Step 5: Create the output data with the suggestions
            // Convert list of suggestions to a single string for display
            String suggestionsText = String.join("\n\n", outfitSuggestions);

            OutfitSuggestionOutputData outputData = new OutfitSuggestionOutputData(
                    suggestionsText,
                    currentUser.getName(),
                    currentTemp,
                    forecast.getCity()
            );

            // Step 6: Send the results to the presenter
            presenter.prepareSuccessView(outputData);

        } catch (Exception e) {
            presenter.prepareFailView("An error occurred: " + e.getMessage());
        }
    }
}