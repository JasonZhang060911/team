package view;

import entity.User;
import interface_adapter.outfit_image_generation.OutfitImageGenerationController;
import interface_adapter.outfit_image_generation.OutfitImageGenerationPresenter;
import interface_adapter.outfit_image_generation.OutfitImageGenerationView;
import use_case.outfit_image_generation.OutfitImageGenerationInputBoundary;
import use_case.outfit_image_generation.OutfitImageGenerationInteractor;
import data_access.outfit_suggestion.OutfitImageGenerationDataAccessObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;

/**
 * Displays AI-generated outfit images in a separate gallery window.
 * Expects Base64-encoded images from the use case.
 */
public class OutfitImageGalleryPanel extends JFrame implements OutfitImageGenerationView {

    private final OutfitImageGenerationController controller;
    private final List<String> outfits;

    private final JPanel imageGrid = new JPanel(new GridLayout(0, 2, 12, 12));
    private final JButton generateButton = new JButton("Generate Outfit Images");
    private final JLabel statusLabel = new JLabel(" ");

    public OutfitImageGalleryPanel(User currentUser, List<String> outfits) {
        this.outfits = outfits;

        OutfitImageGenerationPresenter presenter = new OutfitImageGenerationPresenter(this);
        OutfitImageGenerationDataAccessObject dataAccess =
                new OutfitImageGenerationDataAccessObject(currentUser);
        OutfitImageGenerationInputBoundary interactor =
                new OutfitImageGenerationInteractor(dataAccess, presenter);
        this.controller = new OutfitImageGenerationController(interactor);

        setTitle("Outfit Image Gallery");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        generateButton.addActionListener(e -> requestImages());
        add(generateButton, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(imageGrid);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);

        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    private void requestImages() {
        generateButton.setEnabled(false);
        statusLabel.setText("Generating AI images...");
        imageGrid.removeAll();
        imageGrid.repaint();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.generateImages(outfits);
                return null;
            }

            @Override
            protected void done() {
                generateButton.setEnabled(true);
            }
        }.execute();
    }

    @Override
    public void onImageGenerationSuccess(List<String> base64Images) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Images generated successfully!");
            imageGrid.removeAll();

            if (base64Images == null || base64Images.isEmpty()) {
                imageGrid.add(new JLabel("No images generated."));
            } else {
                for (int i = 0; i < base64Images.size(); i++) {
                    String base64 = base64Images.get(i);

                    JPanel card = new JPanel();
                    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                    card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                    JLabel title = new JLabel("Outfit " + (i + 1));
                    title.setAlignmentX(Component.CENTER_ALIGNMENT);
                    title.setFont(new Font("Arial", Font.BOLD, 14));
                    card.add(title);

                    JLabel imgLabel = new JLabel("Loading image...");
                    imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    card.add(imgLabel);

                    loadBase64ImageAsync(base64, imgLabel);

                    imageGrid.add(card);
                }
            }

            imageGrid.revalidate();
            imageGrid.repaint();
        });
    }

    private void loadBase64ImageAsync(String base64, JLabel targetLabel) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    byte[] decoded = Base64.getDecoder().decode(base64);
                    ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
                    Image img = ImageIO.read(bais);
                    if (img == null) return null;

                    Image scaled = img.getScaledInstance(350, -1, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                } catch (Exception e) {
                    System.err.println("Image decode error: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        targetLabel.setText("");
                        targetLabel.setIcon(icon);
                    } else {
                        targetLabel.setText("Failed to decode image");
                    }
                } catch (Exception e) {
                    targetLabel.setText("Failed to decode image");
                }
            }
        }.execute();
    }

    @Override
    public void onImageGenerationFailure(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, errorMessage,
                    "Image Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Image generation failed.");
        });
    }
}
