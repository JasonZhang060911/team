package use_case.outfit_suggestion;

public class OutfitSuggestionInputData {


// has the info needed to generate personalized outfit suggestions



    private final String username;
    private final String location;

    // constructor
    public OutfitSuggestionInputData(String username, String location) {
        this.username = username;
        this.location = location;
    }

    // username getter
    public String getUsername() {
        return username;
    }
    // location getter
    public String getLocation() {
        return location;
    }
}