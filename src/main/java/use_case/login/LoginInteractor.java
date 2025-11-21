package use_case.login;

import data_access.user_storage.SupabaseUserRepository;
import entity.User;


// Interactor for handling user login
public class LoginInteractor implements LoginInputBoundary{
    private final LoginOutputBoundary outputBoundary;

    public LoginInteractor(LoginOutputBoundary outputBoundary) {
        this.outputBoundary = outputBoundary;
    }

    public void login(LoginInputData inputData) {
        User user = authenticate(inputData.getUsername(), inputData.getPassword());

        if (user == null) {
            LoginOutputData outputData = new LoginOutputData(false, "Invalid username or password", inputData.getUsername());
            outputBoundary.present(outputData);
            return;
        }

        LoginOutputData outputData = new LoginOutputData(true, "Login successful", inputData.getUsername(), user);

        outputBoundary.present(outputData);
    }

    private User authenticate(String username, String password) {
        SupabaseUserRepository userRepository = new SupabaseUserRepository();
        User possibleuser = userRepository.findByUsername(username);
        if (possibleuser == null) {
            return null;
        }
        if (possibleuser.getPassword().equals(password)) {
            return possibleuser;
        }
        return null;
    }
}
