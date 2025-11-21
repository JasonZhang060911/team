package use_case.weather;

/**
 * Input data for the "get today's daily forecast" use case.
 * cityName can be null/empty to indicate "use auto location".
 */
public class DailyForecastInputData {
    public final String cityName;

    public DailyForecastInputData(String cityName) {
        this.cityName = cityName;
    }
}