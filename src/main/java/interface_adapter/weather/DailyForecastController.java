package interface_adapter.weather;

import use_case.weather.DailyForecastInputBoundary;
import use_case.weather.DailyForecastInputData;

/**
 * Controller: validates raw input and delegates to the use case.
 * Keep it thin: basic validation here; API errors handled by interactor.
 */
public class DailyForecastController {

    private final DailyForecastInputBoundary interactor;
    private final WeatherViewModel viewModel;

    public DailyForecastController(DailyForecastInputBoundary interactor,
                                   WeatherViewModel viewModel) {
        this.interactor = interactor;
        this.viewModel = viewModel;
    }

    /** Search by city name. */
    public void searchByCity(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            viewModel.setStatusMessage("Please enter a city name or use 'Use My Location'.");
            viewModel.setSuccess(false);
            return;
        }
        // Optional: allow letters, spaces, hyphens (tune this as you like).
        if (!cityName.matches("^[a-zA-Z\\s\\-]+$")) {
            viewModel.setStatusMessage("Invalid input. Only letters, spaces and '-' are allowed.");
            viewModel.setSuccess(false);
            return;
        }
        interactor.getDailyForecast(new DailyForecastInputData(cityName.trim()));
    }

    /** Use auto-detected location. */
    public void useMyLocation() {
        interactor.getDailyForecast(new DailyForecastInputData(null));
    }
}
