package view;

import data_access.user_storage.UserSession;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import use_case.login.LoginInteractor;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// Login view that observes the LoginViewModel for state changes.

public class LoginPanel extends JFrame implements PropertyChangeListener {
    private final LoginController controller;
    private final LoginViewModel viewModel;
    private Runnable onLoginSuccessCallback;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public LoginPanel() {
        this.viewModel = new LoginViewModel();
        LoginPresenter presenter = new LoginPresenter(viewModel);
        LoginInteractor interactor = new LoginInteractor(presenter);
        this.controller = new LoginController(interactor);

        viewModel.addPropertyChangeListener(this);

        setTitle("Login");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(300, 150);
        setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add username and password fields
        form.add(createFieldPanel("Username:", usernameField));
        form.add(Box.createVerticalStrut(20));
        form.add(createFieldPanel("Password:", passwordField));
        form.add(Box.createVerticalStrut(10));

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> onLogin());

        JButton signupButton = new JButton("Sign Up");
        signupButton.addActionListener(e -> onSignup());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(loginButton);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(signupButton);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }
    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccessCallback = callback;
    }


    public void onLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        controller.login(username, password);
    }

    public void onSignup() {
        dispose();
        SignupPanel signupPanel = new SignupPanel();
        signupPanel.setVisible(true);
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(80, 25));
        panel.add(label);
        panel.add(field);
        return panel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            final LoginState state = viewModel.getState();

            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);


            } else if (state.getUsername() != null && !state.getUsername().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + state.getUsername() + "!");
                if (state.getUser() != null) {
                    // Store user in UserSession
                    UserSession.getInstance().setCurrentUser(state.getUser());
                }

                // Execute callback if set (notify Main that login succeeded)
                if (onLoginSuccessCallback != null) {
                    onLoginSuccessCallback.run();
                }
                dispose();
            }
        }
    }

}
