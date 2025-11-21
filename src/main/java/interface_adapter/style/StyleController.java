package interface_adapter.style;
import use_case.style.StyleInputBoundary;
import use_case.style.StyleInputData;
import java.util.Map;

// Style controller for style preferences
public class StyleController {
    private final StyleInputBoundary interactor;
    public StyleController(StyleInputBoundary interactor) {
        this.interactor = interactor;
    }
    public void saveStylePreferences(String username, Map<String, Boolean> stylePreferences) {
        StyleInputData input = new StyleInputData(username, stylePreferences);
        interactor.saveStylePreferences(input);
    }
}
