package use_case.multiple_outfit_suggestion;

/**
 * Output Boundary for Multiple Outfit Suggestion Use Case.
 * This interface defines how results are presented.
 */
public interface MultipleOutfitSuggestionOutputBoundary {

    /**
     * Prepares the success view with multiple outfit suggestions.
     * @param outputData contains the outfit suggestions and related data
     */
    void prepareSuccessView(MultipleOutfitSuggestionOutputData outputData);

    /**
     * Prepares the failure view with an error message.
     * @param errorMessage the error message to display
     */
    void prepareFailView(String errorMessage);
}
