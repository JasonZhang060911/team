package interface_adapter.outfit_suggestion;

import use_case.outfit_suggestion.OutfitSuggestionInputBoundary;
import use_case.outfit_suggestion.OutfitSuggestionInputData;

// handles button clicks and starts the use case
public class OutfitSuggestionController {

    private final OutfitSuggestionInputBoundary interactor;

    public OutfitSuggestionController(OutfitSuggestionInputBoundary interactor) {
        this.interactor = interactor;
    }

    // called when user clicks "get outfit suggestions" button
    public void execute(String username, String location) {
        OutfitSuggestionInputData inputData = new OutfitSuggestionInputData(username, location);
        interactor.execute(inputData);
    }
}