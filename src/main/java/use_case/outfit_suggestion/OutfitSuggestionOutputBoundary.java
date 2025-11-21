package use_case.outfit_suggestion;

// this interface defines how results are presented

public interface OutfitSuggestionOutputBoundary {

    // prepares the success view with outfit suggestions
    void prepareSuccessView(OutfitSuggestionOutputData outputData);

    // prepares the failure view with an error message
    void prepareFailView(String errorMessage);
}


