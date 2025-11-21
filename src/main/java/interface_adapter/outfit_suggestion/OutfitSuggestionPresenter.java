package interface_adapter.outfit_suggestion;

import use_case.outfit_suggestion.OutfitSuggestionOutputBoundary;
import use_case.outfit_suggestion.OutfitSuggestionOutputData;

// transforms output data into view calls
public class OutfitSuggestionPresenter implements OutfitSuggestionOutputBoundary {

    private final OutfitSuggestionView view;

    public OutfitSuggestionPresenter(OutfitSuggestionView view) {
        this.view = view;
    }

    @Override
    public void prepareSuccessView(OutfitSuggestionOutputData outputData) {
        // call the view with the results
        view.onOutfitSuggestionSuccess(
                outputData.getOutfitSuggestions(),
                outputData.getUsername(),
                outputData.getTemperature(),
                outputData.getCity()
        );
    }

    @Override
    public void prepareFailView(String errorMessage) {
        // call the view with the error
        view.onOutfitSuggestionFailure(errorMessage);
    }
}
