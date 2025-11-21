package view;

import interface_adapter.multiple_outfit_suggestion.MultipleOutfitSuggestionController;
import interface_adapter.multiple_outfit_suggestion.MultipleOutfitSuggestionPresenter;
import interface_adapter.multiple_outfit_suggestion.MultipleOutfitSuggestionView;
import use_case.multiple_outfit_suggestion.MultipleOutfitSuggestionInteractor;
import data_access.multiple_outfit_suggestion.MultipleOutfitSuggestionDataAccessObject;
import data_access.user_storage.UserSession;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel for getting multiple personalized outfit suggestions.
 * Implements Use Case 5: Request Additional Outfit Suggestions.
 */
public class MultipleOutfitSuggestionPanel extends JFrame implements MultipleOutfitSuggestionView {

    private final MultipleOutfitSuggestionController controller;

    private final JSpinner numberOfSuggestionsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
    private final JButton getSuggestionsButton = new JButton("Get Multiple Suggestions");
    private final JTextArea suggestionsArea = new JTextArea(20, 50);
    private final JLabel statusLabel = new JLabel(" ");

    public MultipleOutfitSuggestionPanel() {
        // Get the currently logged-in user (following clean architecture like use case 3)
        User currentUser = UserSession.getInstance().getCurrentUser();

        MultipleOutfitSuggestionPresenter presenter = new MultipleOutfitSuggestionPresenter(this);
        MultipleOutfitSuggestionDataAccessObject dataAccess = new MultipleOutfitSuggestionDataAccessObject();
        MultipleOutfitSuggestionInteractor interactor = new MultipleOutfitSuggestionInteractor(
                currentUser,
                dataAccess,
                presenter
        );
        this.controller = new MultipleOutfitSuggestionController(interactor);

        setTitle("Get Multiple Outfit Suggestions");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel headerLabel = new JLabel("Get Multiple Personalized Outfit Suggestions");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Description
        JLabel descLabel = new JLabel("Generate multiple outfit options based on your style and the weather");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(descLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Input fields
        mainPanel.add(createFieldPanel("# of Suggestions:", numberOfSuggestionsSpinner));
        mainPanel.add(Box.createVerticalStrut(15));

        // Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        getSuggestionsButton.setFont(new Font("Arial", Font.BOLD, 14));
        getSuggestionsButton.setBackground(new Color(70, 130, 180));
        getSuggestionsButton.setForeground(Color.WHITE);
        getSuggestionsButton.setFocusPainted(false);
        getSuggestionsButton.addActionListener(e -> onGetSuggestions());
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.add(getSuggestionsButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Suggestions display area
        JLabel suggestionsLabel = new JLabel("Outfit Suggestions:");
        suggestionsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        suggestionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(suggestionsLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        suggestionsArea.setEditable(false);
        suggestionsArea.setLineWrap(true);
        suggestionsArea.setWrapStyleWord(true);
        suggestionsArea.setFont(new Font("Arial", Font.PLAIN, 13));
        suggestionsArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(suggestionsArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        mainPanel.add(scrollPane);

        add(mainPanel, BorderLayout.CENTER);

        // Status bar at bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(label);
        panel.add(field);
        return panel;
    }

    private void onGetSuggestions() {
        // Get the currently logged-in user from UserSession
        User currentUser = UserSession.getInstance().getCurrentUser();

        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "You must be logged in to get outfit suggestions. Please log in first.",
                    "Not Logged In",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get username and location from the logged-in user
        String username = currentUser.getName();
        String location = currentUser.getLocation();
        int numberOfSuggestions = (Integer) numberOfSuggestionsSpinner.getValue();

        if (location == null || location.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Your profile doesn't have a location set. Please update your profile.",
                    "Missing Location",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Disable button while loading
        getSuggestionsButton.setEnabled(false);
        statusLabel.setText("Loading " + numberOfSuggestions + " suggestions...");
        suggestionsArea.setText("");

        // Run in background thread
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.execute(username, location, numberOfSuggestions);
                return null;
            }

            @Override
            protected void done() {
                // Re-enable button
                getSuggestionsButton.setEnabled(true);
            }
        }.execute();
    }

    @Override
    public void onMultipleOutfitSuggestionSuccess(List<String> suggestions, String username,
                                                  double temperature, String city) {
        SwingUtilities.invokeLater(() -> {
            // Display the suggestions
            StringBuilder display = new StringBuilder();
            display.append("═══════════════════════════════════════════════════════\n");
            display.append("OUTFIT SUGGESTIONS FOR ").append(username.toUpperCase()).append("\n");
            display.append("═══════════════════════════════════════════════════════\n\n");
            display.append("Weather in ").append(city).append(": ")
                    .append(String.format("%.1f", temperature)).append("°C\n\n");
            display.append("───────────────────────────────────────────────────────\n\n");

            for (int i = 0; i < suggestions.size(); i++) {
                display.append(suggestions.get(i));
                if (i < suggestions.size() - 1) {
                    display.append("\n\n───────────────────────────────────────────────────────\n\n");
                }
            }

            display.append("\n\n═══════════════════════════════════════════════════════\n");
            display.append("Total suggestions: ").append(suggestions.size());

            suggestionsArea.setText(display.toString());
            suggestionsArea.setCaretPosition(0);
            statusLabel.setText("Successfully loaded " + suggestions.size() + " suggestions!");
        });
    }

    @Override
    public void onMultipleOutfitSuggestionFailure(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    errorMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Failed to load suggestions");
        });
    }
}
