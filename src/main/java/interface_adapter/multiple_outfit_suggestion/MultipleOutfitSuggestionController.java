package interface_adapter.multiple_outfit_suggestion;

import use_case.multiple_outfit_suggestion.MultipleOutfitSuggestionInputBoundary;
import use_case.multiple_outfit_suggestion.MultipleOutfitSuggestionInputData;

/**
 * Controller for Multiple Outfit Suggestion Use Case.
 * Handles button clicks and starts the use case.
 */
public class MultipleOutfitSuggestionController {

    private final MultipleOutfitSuggestionInputBoundary interactor;

    public MultipleOutfitSuggestionController(MultipleOutfitSuggestionInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Called when user clicks "Get More Suggestions" button.
     * @param username the username of the user
     * @param location the location to get weather for
     * @param numberOfSuggestions the number of outfit suggestions to generate
     */
    public void execute(String username, String location, int numberOfSuggestions) {
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData(username, location, numberOfSuggestions);
        interactor.execute(inputData);
    }
}
