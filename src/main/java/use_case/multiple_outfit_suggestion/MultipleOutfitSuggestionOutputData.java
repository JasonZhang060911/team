package use_case.multiple_outfit_suggestion;

import java.util.List;

/**
 * Output Data for Multiple Outfit Suggestion Use Case.
 * Contains the data returned after generating multiple outfit suggestions.
 */
public class MultipleOutfitSuggestionOutputData {

    private final List<String> outfitSuggestions;
    private final String username;
    private final double temperature;
    private final String city;

    /**
     * Constructor for MultipleOutfitSuggestionOutputData.
     * @param outfitSuggestions list of outfit suggestion strings
     * @param username the username of the user
     * @param temperature the current temperature
     * @param city the city name
     */
    public MultipleOutfitSuggestionOutputData(List<String> outfitSuggestions, String username,
                                              double temperature, String city) {
        this.outfitSuggestions = outfitSuggestions;
        this.username = username;
        this.temperature = temperature;
        this.city = city;
    }

    public List<String> getOutfitSuggestions() {
        return outfitSuggestions;
    }

    public String getUsername() {
        return username;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getCity() {
        return city;
    }
}
