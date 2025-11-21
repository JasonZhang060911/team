package interface_adapter.style;

import interface_adapter.ViewModel;


public class StyleViewModel extends ViewModel<StyleState> {

    public StyleViewModel() {
        super("style preferences");
        setState(new StyleState());
    }
}
