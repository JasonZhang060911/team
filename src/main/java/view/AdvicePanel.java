package view;

import javax.swing.*;
import java.awt.*;

/**
 * AdvicePanel: renders the advice text on the right side.
 */
public class AdvicePanel extends JPanel {

    private final JTextArea textArea = new JTextArea(12, 24);

    public AdvicePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Advice"));

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(textArea);
        add(sp, BorderLayout.CENTER);
    }

    public void setAdviceText(String text) {
        textArea.setText(text != null ? text : "");
        textArea.setCaretPosition(0);
    }

    public void clear() {
        setAdviceText("");
    }
}
