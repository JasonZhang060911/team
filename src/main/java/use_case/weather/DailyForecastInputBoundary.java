package use_case.weather;

/**
 * Input boundary for daily forecast use case.
 */
public interface DailyForecastInputBoundary {
    void getDailyForecast(DailyForecastInputData inputData);
}
