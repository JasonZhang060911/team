package view;

import interface_adapter.weather.WeatherViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ForecastPanel: renders today's four time-slots in a grid.
 * Columns: Label | Icon | Temp | Description | Precip | Wind
 */
public class ForecastPanel extends JPanel {

    private static class Row {
        JLabel label = new JLabel();
        JLabel icon = new JLabel();
        JLabel temp = new JLabel();
        JLabel desc = new JLabel();
        JLabel precip = new JLabel();
        JLabel wind = new JLabel();
    }

    private final List<Row> rows = new ArrayList<>(4);

    public ForecastPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Today"));

        // Header row
        addHeader("Time", "Icon", "Temp", "Description", "Precip", "Wind");

        // Pre-create 4 rows
        for (int i = 0; i < 4; i++) {
            Row r = new Row();
            addRowComponents(i + 1, r);
            rows.add(r);
        }
    }

    /** Render slot data from ViewModel; less than 4 slots will leave blanks. */
    public void render(List<WeatherViewModel.SlotView> slots) {
        // Reset all
        for (Row r : rows) {
            r.label.setText("");
            r.icon.setIcon(null);
            r.temp.setText("");
            r.desc.setText("");
            r.precip.setText("");
            r.wind.setText("");
        }
        if (slots == null) return;

        int n = Math.min(slots.size(), rows.size());
        for (int i = 0; i < n; i++) {
            WeatherViewModel.SlotView s = slots.get(i);
            Row r = rows.get(i);
            r.label.setText(s.label != null ? s.label : "");
            r.temp.setText(s.tempText != null ? s.tempText : "");
            r.desc.setText(s.descText != null ? s.descText : "");
            r.precip.setText(s.precipText != null ? s.precipText : "");
            r.wind.setText(s.windText != null ? s.windText : "");
            // Icon will be set by caller using IconLoader to avoid blocking here
        }
    }

    /** Allows the caller to set icon for a specific row (0..3). */
    public void setRowIcon(int index, Icon icon) {
        if (index >= 0 && index < rows.size()) {
            rows.get(index).icon.setIcon(icon);
        }
    }

    public void clear() {
        render(null);
    }


    private void addHeader(String... cols) {
        GridBagConstraints gbc = base(0, 0);
        gbc.insets = new Insets(4, 8, 4, 8);
        Font f = getFont().deriveFont(Font.BOLD);
        for (int i = 0; i < cols.length; i++) {
            JLabel lb = new JLabel(cols[i]);
            lb.setFont(f);
            gbc.gridx = i;
            add(lb, gbc);
        }
    }

    private void addRowComponents(int gridY, Row r) {
        GridBagConstraints gbc = base(0, gridY);
        gbc.insets = new Insets(6, 8, 6, 8);

        gbc.gridx = 0; add(r.label, gbc);
        gbc.gridx = 1; add(r.icon, gbc);
        gbc.gridx = 2; add(r.temp, gbc);
        gbc.gridx = 3; add(r.desc, gbc);
        gbc.gridx = 4; add(r.precip, gbc);
        gbc.gridx = 5; add(r.wind, gbc);
    }

    private GridBagConstraints base(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        return gbc;
    }
}
