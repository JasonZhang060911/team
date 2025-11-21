package use_case.weather;

import java.util.List;

/**
 * Output data for "get today's daily forecast".
 * success=false with message indicates an error (e.g., city not found).
 */
public class DailyForecastOutputData {
    public final String city;
    public final List<SlotDTO> slots;  // expected size 4 (Morning/Afternoon/Evening/Overnight)
    public final String adviceText;    // generated advice text
    public final boolean success;      // true if everything ok
    public final String message;       // error or info message

    public DailyForecastOutputData(String city,
                                   List<SlotDTO> slots,
                                   String adviceText,
                                   boolean success,
                                   String message) {
        this.city = city;
        this.slots = slots;
        this.adviceText = adviceText;
        this.success = success;
        this.message = message;
    }
}
