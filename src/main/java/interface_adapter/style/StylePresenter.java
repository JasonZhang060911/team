package interface_adapter.style;

import use_case.style.StyleOutputBoundary;
import use_case.style.StyleOutputData;


// presenter that transforms style output data into view model updates
public class StylePresenter implements StyleOutputBoundary {
    private final StyleViewModel viewModel;

    public StylePresenter(StyleViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(StyleOutputData output) {
        final StyleState state = viewModel.getState();

        state.setMessage(output.getMessage());
        state.setSuccess(output.isSuccess());

        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}


