package interface_adapter.signup;

import use_case.signup.SignupOutputBoundary;
import use_case.signup.SignupOutputData;

// presenter that transforms signup output data into view model updates
public class SignupPresenter implements SignupOutputBoundary {
    private final SignupViewModel viewModel;

    public SignupPresenter(SignupViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(SignupOutputData output) {
        final SignupState state = viewModel.getState();

        if (output.isSuccess()) {
            state.setUsername(output.getUsername());
            state.setErrorMessage("");
        } else {
            state.setUsername("");
            state.setErrorMessage(output.getMessage());
        }

        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}
