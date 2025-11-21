package use_case.login;

import entity.User;

public class LoginOutputData {
    private final boolean success;
    private final String message;
    private final String username;
    private final User user;

    public LoginOutputData(boolean success, String message, String username) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.user = null;
    }

    public LoginOutputData(boolean success, String message, String username, User user) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public User getUser() {
        return user;
    }

}
