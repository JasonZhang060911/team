package interface_adapter.login;

import use_case.login.LoginInputBoundary;
import use_case.login.LoginInputData;

// Controller in the interface adapter layer for handling login requests
public class LoginController {
    private final LoginInputBoundary interactor;

    public LoginController(LoginInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void login(String username, String password) {
        LoginInputData input = new LoginInputData(username, password);
        interactor.login(input);
    }
}
