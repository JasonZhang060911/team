package use_case.multiple_outfit_suggestion;

import entity.User;
import entity.DailyForecast;
import entity.ForecastSlot;
import java.util.List;

/**
 * The Multiple Outfit Suggestion Interactor.
 * This contains the business logic for generating multiple personalized outfit suggestions
 * based on user preferences and current weather conditions.
 */
public class MultipleOutfitSuggestionInteractor implements MultipleOutfitSuggestionInputBoundary {

    private final User currentUser;
    private final MultipleOutfitSuggestionDataAccessInterface weatherAndAIAccess;
    private final MultipleOutfitSuggestionOutputBoundary presenter;

    /**
     * Constructor for MultipleOutfitSuggestionInteractor.
     * @param currentUser the logged in user with their preferences
     * @param weatherAndAIAccess the data access for weather and AI suggestions
     * @param presenter the presenter for showing results to the user
     */
    public MultipleOutfitSuggestionInteractor(
            User currentUser,
            MultipleOutfitSuggestionDataAccessInterface weatherAndAIAccess,
            MultipleOutfitSuggestionOutputBoundary presenter) {
        this.currentUser = currentUser;
        this.weatherAndAIAccess = weatherAndAIAccess;
        this.presenter = presenter;
    }

    /**
     * Executes the multiple outfit suggestion use case.
     * @param inputData contains the username, location, and number of suggestions
     */
    @Override
    public void execute(MultipleOutfitSuggestionInputData inputData) {
        try {
            // Step 1: Use the already-logged-in user (no database query needed!)
            // The user object is passed in the constructor with all their preferences already loaded

            // Step 2: Get today's weather forecast for the location
            DailyForecast forecast = weatherAndAIAccess.getWeatherForecast(inputData.getLocation());

            if (forecast == null || forecast.getSlots().isEmpty()) {
                presenter.prepareFailView("Could not retrieve weather data for " + inputData.getLocation());
                return;
            }

            // Step 3: Generate multiple outfit suggestions using the LLM
            // This combines user preferences (style, gender, etc.) with weather data
            List<String> outfitSuggestions = weatherAndAIAccess.generateMultipleOutfitSuggestions(
                    currentUser,
                    forecast,
                    inputData.getNumberOfSuggestions()
            );

            if (outfitSuggestions == null || outfitSuggestions.isEmpty()) {
                presenter.prepareFailView("Could not generate outfit suggestions. Please try again.");
                return;
            }

            // Step 4: Get temperature info for display (from first slot)
            ForecastSlot firstSlot = forecast.getSlots().get(0);
            double currentTemp = firstSlot.getTemperature();

            // Step 5: Create the output data with the suggestions
            MultipleOutfitSuggestionOutputData outputData = new MultipleOutfitSuggestionOutputData(
                    outfitSuggestions,
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
