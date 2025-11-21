package use_case.weather;

/**
 * Output boundary for daily forecast use case.
 */
public interface DailyForecastOutputBoundary {
    void presentDailyForecast(DailyForecastOutputData outputData);
}
