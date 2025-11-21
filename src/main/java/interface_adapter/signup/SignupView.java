package interface_adapter.signup;

// View interface used by presenter to communicate with the Signup View
public interface SignupView {
    void onSignupFailure(String message);
    void onSignupSuccess(String username);
}
