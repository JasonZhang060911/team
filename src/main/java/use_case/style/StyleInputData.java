package use_case.style;

import java.util.Map;

public class StyleInputData {
    private final String username;
    private final Map<String, Boolean> stylePreferences;

    public StyleInputData(String username, Map<String, Boolean> stylePreferences) {
        this.username = username;
        this.stylePreferences = stylePreferences;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Boolean> getStylePreferences() {
        return stylePreferences;
    }
}

