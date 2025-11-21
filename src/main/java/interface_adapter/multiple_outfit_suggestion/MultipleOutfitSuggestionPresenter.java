package interface_adapter.multiple_outfit_suggestion;

import use_case.multiple_outfit_suggestion.MultipleOutfitSuggestionOutputBoundary;
import use_case.multiple_outfit_suggestion.MultipleOutfitSuggestionOutputData;
import java.util.List;

/**
 * Presenter for Multiple Outfit Suggestion Use Case.
 * Transforms output data into view calls.
 */
public class MultipleOutfitSuggestionPresenter implements MultipleOutfitSuggestionOutputBoundary {

    private final MultipleOutfitSuggestionView view;

    public MultipleOutfitSuggestionPresenter(MultipleOutfitSuggestionView view) {
        this.view = view;
    }

    @Override
    public void prepareSuccessView(MultipleOutfitSuggestionOutputData outputData) {
        // Call the view with the results
        view.onMultipleOutfitSuggestionSuccess(
                outputData.getOutfitSuggestions(),
                outputData.getUsername(),
                outputData.getTemperature(),
                outputData.getCity()
        );
    }

    @Override
    public void prepareFailView(String errorMessage) {
        // Call the view with the error
        view.onMultipleOutfitSuggestionFailure(errorMessage);
    }
}
