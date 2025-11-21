package use_case.outfit_suggestion;

// this interface defines what the interactor must be able to do

public interface OutfitSuggestionInputBoundary {

    // execute the outfit suggestion use case
    void execute(OutfitSuggestionInputData inputData);
}
