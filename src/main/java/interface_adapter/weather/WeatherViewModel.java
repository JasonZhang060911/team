package interface_adapter.weather;

import java.util.ArrayList;
import java.util.List;

/**
 * WeatherViewModel: UI state container for daily forecast view.
 * No Swing types here. The View reads these strings and icon codes to render.
 */
public class WeatherViewModel {

    /** Single row in the 4-slot grid. */
    public static class SlotView {
        public final String label;      // "Morning"
        public final String tempText;   // "12.3℃"
        public final String descText;   // "light rain (feels like 10.5℃)"
        public final String iconCode;   // "10d"
        public final String precipText; // "40%"
        public final String windText;   // "3.2 m/s"

        public SlotView(String label, String tempText, String descText,
                        String iconCode, String precipText, String windText) {
            this.label = label;
            this.tempText = tempText;
            this.descText = descText;
            this.iconCode = iconCode;
            this.precipText = precipText;
            this.windText = windText;
        }
    }

    private String city = "";
    private List<SlotView> todaySlots = new ArrayList<>();
    private String adviceText = "";
    private String statusMessage = ""; // e.g., "OK" / error message
    private boolean success = true;

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city != null ? city : ""; }

    public List<SlotView> getTodaySlots() { return todaySlots; }
    public void setTodaySlots(List<SlotView> todaySlots) {
        this.todaySlots = (todaySlots != null) ? todaySlots : new ArrayList<>();
    }

    public String getAdviceText() { return adviceText; }
    public void setAdviceText(String adviceText) {
        this.adviceText = adviceText != null ? adviceText : "";
    }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage != null ? statusMessage : "";
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
