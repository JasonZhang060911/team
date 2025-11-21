package use_case.multiple_outfit_suggestion;

/**
 * Input Boundary for Multiple Outfit Suggestion Use Case.
 * This interface defines what the interactor must be able to do.
 */
public interface MultipleOutfitSuggestionInputBoundary {

    /**
     * Executes the multiple outfit suggestion use case.
     * @param inputData contains the username, location, and number of suggestions requested
     */
    void execute(MultipleOutfitSuggestionInputData inputData);
}
