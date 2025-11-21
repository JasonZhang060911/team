package interface_adapter.weather;

import use_case.weather.DailyForecastOutputBoundary;
import use_case.weather.DailyForecastOutputData;
import use_case.weather.SlotDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter: converts OutputData into UI-friendly ViewModel values.
 * Keep it UI-agnostic (no Swing types), only simple strings and codes.
 */
public class DailyForecastPresenter implements DailyForecastOutputBoundary {

    private final WeatherViewModel viewModel;

    public DailyForecastPresenter(WeatherViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentDailyForecast(DailyForecastOutputData outputData) {

        viewModel.setCity(outputData.city);

        viewModel.setAdviceText(outputData.adviceText != null ? outputData.adviceText : "");

        viewModel.setStatusMessage(outputData.message != null ? outputData.message : "");
        viewModel.setSuccess(outputData.success);

        // Slots → SlotView list
        List<WeatherViewModel.SlotView> slotViews = new ArrayList<>();
        if (outputData.slots != null) {
            for (SlotDTO s : outputData.slots) {
                String tempText = String.format("%.1f℃", s.temperature);
                String precipText = (s.precipProbability == null) ? ""
                        : String.format("%.0f%%", s.precipProbability * 100.0);
                String windText = (s.windSpeed == null) ? ""
                        : String.format("%.1f m/s", s.windSpeed);
                // Feels-like is optional; we can append if present
                String descText = s.description;
                if (s.feelsLike != null) {
                    descText = String.format("%s (feels like %.1f℃)", s.description, s.feelsLike);
                }

                slotViews.add(new WeatherViewModel.SlotView(
                        s.label, tempText, descText, s.iconCode, precipText, windText
                ));
            }
        }
        viewModel.setTodaySlots(slotViews);
    }
}
