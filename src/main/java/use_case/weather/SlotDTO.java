package use_case.weather;

/**
 * SlotDTO: use-case level data for a single time-slot.
 * Keep it UI-agnostic (raw numbers), Presenter will format them for UI.
 */
public class SlotDTO {
    public final String label;             // "Morning"/"Afternoon"/...
    public final double temperature;       // Celsius
    public final String description;
    public final String iconCode;          // e.g., "10d"
    public final Double precipProbability; // 0..1
    public final Double windSpeed;         // m/s
    public final Double feelsLike;         // Celsius

    public SlotDTO(String label,
                   double temperature,
                   String description,
                   String iconCode,
                   Double precipProbability,
                   Double windSpeed,
                   Double feelsLike) {
        this.label = label;
        this.temperature = temperature;
        this.description = description;
        this.iconCode = iconCode;
        this.precipProbability = precipProbability;
        this.windSpeed = windSpeed;
        this.feelsLike = feelsLike;
    }
}