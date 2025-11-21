package view;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupState;
import interface_adapter.signup.SignupViewModel;
import data_access.user_storage.SupabaseUserRepository;
import use_case.signup.SignupInteractor;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// Signup view that observes the SignupViewModel for state changes.

public class SignupPanel extends JFrame implements PropertyChangeListener {
    private final SignupController controller;
    private final SignupViewModel viewModel;
    private final SupabaseUserRepository repository;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JTextField locationField = new JTextField(20);
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Prefer not to say", "Male", "Female", "Other"});

    public SignupPanel() {
        this.repository = new SupabaseUserRepository();

        this.viewModel = new SignupViewModel();
        SignupPresenter presenter = new SignupPresenter(viewModel);
        SignupInteractor interactor = new SignupInteractor(repository, presenter);
        this.controller = new SignupController(interactor);

        viewModel.addPropertyChangeListener(this);

        setTitle("Signup");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add username, password, location, and gender fields
        form.add(createFieldPanel("Username:", usernameField));
        form.add(Box.createVerticalStrut(10));
        form.add(createFieldPanel("Password:", passwordField));
        form.add(Box.createVerticalStrut(10));
        form.add(createFieldPanel("Location:", locationField));
        form.add(Box.createVerticalStrut(10));
        form.add(createFieldPanel("Gender:", genderBox));

        JButton signupButton = new JButton("Sign Up");
        signupButton.addActionListener(e -> onSignup());

        JPanel bottom = new JPanel();
        bottom.add(signupButton);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(80, 25));
        panel.add(label);
        panel.add(field);
        return panel;
    }

    private void onSignup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String location = locationField.getText();
        String gender = (String) genderBox.getSelectedItem();

        controller.register(username, password, location, gender);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            final SignupState state = viewModel.getState();

            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Signup Error", JOptionPane.ERROR_MESSAGE);
            } else if (state.getUsername() != null && !state.getUsername().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful! Now let's set up your style preferences.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dispose();

                StylePanel stylePanel = new StylePanel(state.getUsername(), repository);
                stylePanel.setVisible(true);
            }
        }
    }
}
