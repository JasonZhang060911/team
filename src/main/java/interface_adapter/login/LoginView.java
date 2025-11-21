package interface_adapter.login;

public interface LoginView {
    void onLoginSuccess(String username);
    void onLoginFailure(String message);
}
