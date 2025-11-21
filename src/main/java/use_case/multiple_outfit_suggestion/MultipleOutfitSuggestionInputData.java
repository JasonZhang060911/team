package use_case.multiple_outfit_suggestion;

/**
 * Input Data for Multiple Outfit Suggestion Use Case.
 * Contains the information needed to generate multiple personalized outfit suggestions.
 */
public class MultipleOutfitSuggestionInputData {

    private final String username;
    private final String location;
    private final int numberOfSuggestions;

    /**
     * Constructor for MultipleOutfitSuggestionInputData.
     * @param username the username of the user requesting suggestions
     * @param location the location to get weather data for
     * @param numberOfSuggestions the number of different outfit suggestions to generate
     */
    public MultipleOutfitSuggestionInputData(String username, String location, int numberOfSuggestions) {
        this.username = username;
        this.location = location;
        this.numberOfSuggestions = numberOfSuggestions;
    }

    public String getUsername() {
        return username;
    }

    public String getLocation() {
        return location;
    }

    public int getNumberOfSuggestions() {
        return numberOfSuggestions;
    }
}
