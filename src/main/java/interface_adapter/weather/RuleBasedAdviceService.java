package interface_adapter.weather;

import entity.DailyForecast;
import entity.ForecastSlot;
import use_case.weather.AdviceService;

import java.util.Comparator;
import java.util.Locale;

/**
 * RuleBasedAdviceService: simple, transparent rules to generate advice text.
 * You can later replace this with an LLM-based implementation without changing the use case.
 */
public class RuleBasedAdviceService implements AdviceService {

    @Override
    public String makeAdvice(DailyForecast forecast) {
        StringBuilder sb = new StringBuilder();

        // Find extremes
        double minTemp = forecast.getSlots().stream()
                .map(ForecastSlot::getTemperature).min(Comparator.naturalOrder()).orElse(Double.NaN);
        double maxTemp = forecast.getSlots().stream()
                .map(ForecastSlot::getTemperature).max(Comparator.naturalOrder()).orElse(Double.NaN);

        boolean willRain = forecast.getSlots().stream().anyMatch(s ->
                (s.getPrecipProbability() != null && s.getPrecipProbability() >= 0.5)
                        || s.getDescription().toLowerCase(Locale.ROOT).contains("rain")
                        || s.getDescription().toLowerCase(Locale.ROOT).contains("drizzle"));

        boolean windy = forecast.getSlots().stream().anyMatch(s ->
                s.getWindSpeed() != null && s.getWindSpeed() >= 10.0); // ~36 km/h

        // Compose rules
        if (willRain) sb.append("It may rain today, bring an umbrella. ");
        if (!Double.isNaN(minTemp) && minTemp < 5) sb.append("It's quite cold at times, wear warm layers. ");
        if (!Double.isNaN(maxTemp) && maxTemp > 28) sb.append("It may feel hot, stay hydrated. ");
        if (!Double.isNaN(minTemp) && !Double.isNaN(maxTemp) && (maxTemp - minTemp) >= 8) {
            sb.append("Large temperature swing; consider dressing in layers. ");
        }
        if (windy) sb.append("It will be windy; secure hats or consider windproof outerwear. ");

        if (sb.length() == 0) {
            sb.append("Weather looks moderate today. Dress comfortably and enjoy your day.");
        }
        return sb.toString().trim();
    }
}