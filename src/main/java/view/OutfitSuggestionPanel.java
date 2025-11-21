package view;

import interface_adapter.outfit_suggestion.OutfitSuggestionController;
import interface_adapter.outfit_suggestion.OutfitSuggestionPresenter;
import interface_adapter.outfit_suggestion.OutfitSuggestionView;
import use_case.outfit_suggestion.OutfitSuggestionInteractor;
import entity.User;
import data_access.outfit_suggestion.OutfitSuggestionDataAccessObject;

import javax.swing.*;
import java.awt.*;

// panel for getting personalized outfit suggestions
public class OutfitSuggestionPanel extends JFrame implements OutfitSuggestionView {

    private final OutfitSuggestionController controller;
    private final User currentUser;

    private final JTextField locationField = new JTextField(20);
    private final JButton getSuggestionsButton = new JButton("Get Outfit Suggestions");
    private final JTextArea suggestionsArea = new JTextArea(15, 40);
    private final JLabel statusLabel = new JLabel(" ");

    public OutfitSuggestionPanel(User currentUser) {
        this.currentUser = currentUser;
        // set up clean architecture components
        OutfitSuggestionPresenter presenter = new OutfitSuggestionPresenter(this);
        OutfitSuggestionDataAccessObject dataAccess = new OutfitSuggestionDataAccessObject();
        OutfitSuggestionInteractor interactor = new OutfitSuggestionInteractor(
                currentUser,  // first parameter
                dataAccess,      // second parameter
                presenter        // third parameter
        );
        this.controller = new OutfitSuggestionController(interactor);

        setTitle("Get Outfit Suggestions for " + currentUser.getName()); // personalized title
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // header
        JLabel headerLabel = new JLabel("Get Personalized Outfit Suggestions");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // input field (just location cuz user is already logged in)

//        mainPanel.add(createFieldPanel("Location:", locationField));
//        mainPanel.add(Box.createVerticalStrut(15));

        // button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        getSuggestionsButton.setFont(new Font("Arial", Font.BOLD, 14));
        getSuggestionsButton.addActionListener(e -> onGetSuggestions());
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.add(getSuggestionsButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // suggestions display area
        JLabel suggestionsLabel = new JLabel("Outfit Suggestions:");
        suggestionsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        suggestionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(suggestionsLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        suggestionsArea.setEditable(false);
        suggestionsArea.setLineWrap(true);
        suggestionsArea.setWrapStyleWord(true);
        suggestionsArea.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(suggestionsArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(scrollPane);
        scrollPane.setPreferredSize(new Dimension(350, 450));

        add(mainPanel, BorderLayout.CENTER);

        // status bar at bottom
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
        label.setPreferredSize(new Dimension(80, 25));
        panel.add(label);
        panel.add(field);
        return panel;
    }

    private void onGetSuggestions() {
//        String location = locationField.getText().trim();

        String location = currentUser.getLocation().trim();

        if ( location.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a location",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // disable button while loading
        getSuggestionsButton.setEnabled(false);
        statusLabel.setText("Loading suggestions for " + currentUser.getName() + " ... ");
        suggestionsArea.setText("");

        // run in background thread
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.execute(currentUser.getName(), location);
                return null;
            }

            @Override
            protected void done() {
                // re-enable button
                getSuggestionsButton.setEnabled(true);
            }
        }.execute();
    }

    @Override
    public void onOutfitSuggestionSuccess(String suggestions, String username,
                                          double temperature, String city) {
        SwingUtilities.invokeLater(() -> {
            // display the suggestions
            StringBuilder display = new StringBuilder();
            display.append("Weather in ").append(city).append(": ")
                    .append(String.format("%.1f", temperature)).append("°C\n\n");
            display.append(suggestions);

            suggestionsArea.setText(display.toString());
            suggestionsArea.setCaretPosition(0);
            statusLabel.setText("Suggestions loaded successfully!");
        });
    }

    @Override
    public void onOutfitSuggestionFailure(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    errorMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Failed to load suggestions");
        });
    }
}