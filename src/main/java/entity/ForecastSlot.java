package entity;

/**
 * ForecastSlot: one time-slot (e.g., Morning/Afternoon/Evening/Overnight) of today's weather.
 * Pure domain object, no UI/HTTP dependencies.
 */
public class ForecastSlot {
    private final String label;           // e.g., "Morning"
    private final double temperature;     // Celsius
    private final String description;     // short weather description
    private final String iconCode;        // OWM icon code, e.g., "10d"
    private final Double precipProbability; // 0..1, nullable if unavailable
    private final Double windSpeed;       // m/s, nullable
    private final Double feelsLike;       // Celsius, nullable

    public ForecastSlot(String label,
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

    public String getLabel() { return label; }
    public double getTemperature() { return temperature; }
    public String getDescription() { return description; }
    public String getIconCode() { return iconCode; }
    public Double getPrecipProbability() { return precipProbability; }
    public Double getWindSpeed() { return windSpeed; }
    public Double getFeelsLike() { return feelsLike; }
}