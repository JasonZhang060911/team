package interface_adapter.signup;

import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInputData;

// Controller in the interface adapter layer for handling signup requests
public class SignupController {
    private final SignupInputBoundary interactor;

    public SignupController(SignupInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void register(String username, String password, String location, String gender) {
        SignupInputData input = new SignupInputData(username, password, location, gender);
        interactor.register(input);
    }
}

