package interface_adapter.login;

import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;

// presenter transforms login output data into view model updates
public class LoginPresenter implements LoginOutputBoundary {
    private final LoginViewModel viewModel;

    public LoginPresenter(LoginViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(LoginOutputData outputData) {
        final LoginState state = viewModel.getState();

        if (outputData.isSuccess()) {
            state.setUsername(outputData.getUsername());
            state.setErrorMessage("");
            state.setUser(outputData.getUser());
        } else {
            state.setUsername("");
            state.setErrorMessage(outputData.getMessage());
            state.setUser(null);
        }

        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}
