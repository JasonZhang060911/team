package data_access.user_storage;

// manages the current user that is logged in
// singleton class to ensure only one instance of current user exists

import entity.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {
        // Private constructor to prevent instantiation
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void clearSession() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}

