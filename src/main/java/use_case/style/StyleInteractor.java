package use_case.style;

import data_access.user_storage.PendingUserStorage;
import data_access.user_storage.SupabaseUserRepository;
import entity.User;
import use_case.weather.UserRepository;


// Interactor for saving user style preferences.

public class StyleInteractor implements StyleInputBoundary {
    private final UserRepository repository;
    private final StyleOutputBoundary presenter;
    private final SupabaseUserRepository supabaseRepository;

    public StyleInteractor(UserRepository repository, StyleOutputBoundary presenter) {
        this.repository = repository;
        this.presenter = presenter;
        this.supabaseRepository = new SupabaseUserRepository();
    }

    @Override
    public void saveStylePreferences(StyleInputData input) {
        // retrieve user from pending storage
        User user = PendingUserStorage.getInstance().getPendingUser(input.getUsername());

        // Update style preferences
        user.setStyle(input.getStylePreferences());

        // Save user to database
        supabaseRepository.save(user);

        // Remove from pending storage
        PendingUserStorage.getInstance().removePendingUser(input.getUsername());

        presenter.present(new StyleOutputData(true, "Style preferences saved successfully!", input.getUsername()));
    }
}
