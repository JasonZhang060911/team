package use_case.signup;

import data_access.user_storage.PendingUserStorage;
import entity.User;
import use_case.weather.UserRepository;


// Interactor for user signup

public class SignupInteractor implements SignupInputBoundary {
    private final UserRepository repository;
    private final SignupOutputBoundary presenter;

    public SignupInteractor(UserRepository repository, SignupOutputBoundary presenter) {
        this.repository = repository;
        this.presenter = presenter;
    }

    @Override
    public void register(SignupInputData input) {
        // Basic validation
        if (input.getUsername() == null || input.getUsername().trim().isEmpty()) {
            presenter.present(new SignupOutputData(false, "Username cannot be empty", null));
            return;
        }
        if (input.getPassword() == null || input.getPassword().trim().isEmpty()) {
            presenter.present(new SignupOutputData(false, "Password cannot be empty", null));
            return;
        }

        // Check existing user
        if (repository.exists(input.getUsername())) {
            presenter.present(new SignupOutputData(false, "Username already taken", null));
            return;
        }

        // Create user and store in pending storage
        User user = new User(input.getUsername(), input.getPassword(), input.getLocation(), input.getGender());
        PendingUserStorage.getInstance().storePendingUser(user);

        // User will be saved to database after style preferences are added in StyleInteractor
        presenter.present(new SignupOutputData(true, "Registration successful for " + input.getUsername(), input.getUsername()));
    }
}

