package interface_adapter.outfit_suggestion;

// interface that the view must implement
public interface OutfitSuggestionView {

    // called when outfit suggestions are successfully generated
    void onOutfitSuggestionSuccess(String suggestions, String username,
                                   double temperature, String city);

    // called when something goes wrong
    void onOutfitSuggestionFailure(String errorMessage);
}