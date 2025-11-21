package view;

import interface_adapter.style.StyleController;
import interface_adapter.style.StylePresenter;
import interface_adapter.style.StyleState;
import interface_adapter.style.StyleViewModel;
import use_case.style.StyleInteractor;
import use_case.weather.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;


 // Style preferences view that observes the StyleViewModel for state changes.

public class StylePanel extends JFrame implements PropertyChangeListener {
    private final StyleController controller;
    private final StyleViewModel viewModel;
    private final String username;

    // Checkboxes for clothing items
    private final Map<String, JCheckBox> checkboxes = new HashMap<>();

    public StylePanel(String username, UserRepository repository) {
        this.username = username;


        this.viewModel = new StyleViewModel();
        StylePresenter presenter = new StylePresenter(viewModel);
        StyleInteractor interactor = new StyleInteractor(repository, presenter);
        this.controller = new StyleController(interactor);


        viewModel.addPropertyChangeListener(this);

        setTitle("Select Your Style Preferences");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panel with sections
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Choose the clothing items you like:");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // add each categories
        addCategory(mainPanel, "Bottoms", new String[]{
            "jeans", "sweatpants", "shorts", "dress pants/chinos", "leggings", "skirts"
        });

        addCategory(mainPanel, "Tops", new String[]{
            "T-shirts", "long sleeve shirts", "tank tops", "polo shirts", "blouses"
        });

        addCategory(mainPanel, "Outerwear", new String[]{
            "hoodie", "sweatshirt/crewneck", "light jacket/windbreaker",
            "denim jacket", "winter coat/puffer", "raincoat", "blazer"
        });

        addCategory(mainPanel, "Footwear", new String[]{
            "sneakers", "casual shoes", "boots", "sandals/slippers", "dress shoes", "crocs"
        });

        addCategory(mainPanel, "Accessories", new String[]{
            "hats/caps/beanies", "scarf", "gloves", "belt", "sunglasses", "watch"
        });

        // wrap main panel in scroll panel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // save button
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Preferences");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.addActionListener(e -> onSave());
        buttonPanel.add(saveButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(500, 600);
        setLocationRelativeTo(null);
    }

    private void addCategory(JPanel parent, String categoryName, String[] items) {
        // category label
        JLabel categoryLabel = new JLabel(categoryName + ":");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(categoryLabel);
        parent.add(Box.createVerticalStrut(5));

        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        for (String item : items) {
            JCheckBox checkbox = new JCheckBox(item);
            checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);
            checkboxes.put(item, checkbox);
            categoryPanel.add(checkbox);
        }

        parent.add(categoryPanel);
        parent.add(Box.createVerticalStrut(15));
    }

    private void onSave() {
        Map<String, Boolean> stylePreferences = new HashMap<>();
        for (Map.Entry<String, JCheckBox> entry : checkboxes.entrySet()) {
            stylePreferences.put(entry.getKey(), entry.getValue().isSelected());
        }

        controller.saveStylePreferences(username, stylePreferences);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            final StyleState state = viewModel.getState();

            if (state.isSuccess()) {
                    JOptionPane.showMessageDialog(this, state.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Close the style frame
                    LoginPanel loginPanel = new LoginPanel();
                    loginPanel.setVisible(true);
            } else if (state.getMessage() != null && !state.getMessage().isEmpty()) {
                    JOptionPane.showMessageDialog(this, state.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

