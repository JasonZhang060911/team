package use_case.outfit_suggestion;

// data returned after generating outfit suggestions
public class OutfitSuggestionOutputData {

    private final String outfitSuggestions;
    private final String username;
    private final double temperature;
    private final String city;

    public OutfitSuggestionOutputData(String outfitSuggestions, String username,
                                      double temperature, String city) {
        this.outfitSuggestions = outfitSuggestions;
        this.username = username;
        this.temperature = temperature;
        this.city = city;
    }

    public String getOutfitSuggestions() {
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