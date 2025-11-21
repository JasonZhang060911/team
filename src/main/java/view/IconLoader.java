package view;

import javax.swing.*;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IconLoader: map OWM icon code to ImageIcon with a simple cache.
 */
public class IconLoader {

    private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

    public static ImageIcon getIcon(String iconCode) {
        if (iconCode == null || iconCode.isEmpty()) return null;
        return CACHE.computeIfAbsent(iconCode, code -> {
            try {
                String url = "https://openweathermap.org/img/wn/" + code + "@2x.png";
                return new ImageIcon(new URL(url));
            } catch (Exception e) {
                return null;
            }
        });
    }
}
