package interface_adapter.login;

import entity.User;

// State info representiong login process
public class LoginState {
    private String username = "";
    private String errorMessage = "";
    private User user = null;

    public LoginState() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}