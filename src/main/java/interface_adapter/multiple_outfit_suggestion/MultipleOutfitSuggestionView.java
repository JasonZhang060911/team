package interface_adapter.multiple_outfit_suggestion;

import java.util.List;

/**
 * View Interface for Multiple Outfit Suggestion Use Case.
 * Defines the callbacks that the presenter can call to update the UI.
 */
public interface MultipleOutfitSuggestionView {

    /**
     * Called when multiple outfit suggestions are successfully generated.
     * @param suggestions list of outfit suggestion strings
     * @param username the username of the user
     * @param temperature the current temperature
     * @param city the city name
     */
    void onMultipleOutfitSuggestionSuccess(List<String> suggestions, String username,
                                           double temperature, String city);

    /**
     * Called when outfit suggestion generation fails.
     * @param errorMessage the error message to display
     */
    void onMultipleOutfitSuggestionFailure(String errorMessage);
}
