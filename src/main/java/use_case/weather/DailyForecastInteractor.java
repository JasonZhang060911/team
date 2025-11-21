package use_case.weather;

import entity.DailyForecast;
import entity.ForecastSlot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * DailyForecastInteractor: orchestrates the "today's four-slot forecast" logic.
 */
public class DailyForecastInteractor implements DailyForecastInputBoundary {

    private final ForecastAPIGateway forecastGateway;
    private final LocationService locationService;
    private final AdviceService adviceService;
    private final DailyForecastOutputBoundary presenter;

    public DailyForecastInteractor(ForecastAPIGateway forecastGateway,
                                   LocationService locationService,
                                   AdviceService adviceService,
                                   DailyForecastOutputBoundary presenter) {
        this.forecastGateway = forecastGateway;
        this.locationService = locationService;
        this.adviceService = adviceService;
        this.presenter = presenter;
    }

    @Override
    public void getDailyForecast(DailyForecastInputData inputData) {
        String resolvedCity = (inputData.cityName == null || inputData.cityName.trim().isEmpty())
                ? null : inputData.cityName.trim();

        try {
            //Resolve city (auto location if not provided)
            if (resolvedCity == null) {
                resolvedCity = locationService.getCurrentCity();
            }

            //Call gateway to get 5-day/3-hour forecast JSON
            String json = forecastGateway.request3hForecastJson(resolvedCity);

            //Parse JSON
            JSONObject root = new JSONObject(json);
            JSONObject cityObj = root.getJSONObject("city");
            int tzShiftSec = cityObj.optInt("timezone", 0); // seconds from UTC
            ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(tzShiftSec);

            // "today" in city's local time
            LocalDate cityToday = Instant.now().atOffset(zoneOffset).toLocalDate();

            JSONArray arr = root.getJSONArray("list"); // 3h forecast entries

            // Find the earliest available date in the forecast
            LocalDate earliestDate = null;
            if (arr.length() > 0) {
                long firstDt = arr.getJSONObject(0).getLong("dt");
                earliestDate = Instant.ofEpochSecond(firstDt).atOffset(zoneOffset).toLocalDate();
            }

            // Use the earliest available date (which should be today or the next available day)
            LocalDate targetDate = earliestDate != null ? earliestDate : cityToday;

            // Collect entries for the target date
            List<JSONObject> todays = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                long dt = item.getLong("dt"); // Unix time (seconds)
                LocalDateTime local = Instant.ofEpochSecond(dt).atOffset(zoneOffset).toLocalDateTime();
                if (local.toLocalDate().equals(targetDate)) {
                    // Attach local datetime for later sorting/selection (store as string field)
                    item.put("_localHour", local.getHour());
                    item.put("_localDateTime", local.toString());
                    todays.add(item);
                }
            }

            if (todays.isEmpty()) {
                // No data for today — treat as error
                presenter.presentDailyForecast(new DailyForecastOutputData(
                        resolvedCity, new ArrayList<>(),
                        "No forecast available for today.", false,
                        "No forecast entries for today."
                ));
                return;
            }

            //Pick 4 representative slots (closest to target hours)
            ForecastSlot morning   = pickSlotForTargetHour(todays, zoneOffset, targetDate,  9, "Morning");
            ForecastSlot afternoon = pickSlotForTargetHour(todays, zoneOffset, targetDate, 15, "Afternoon");
            ForecastSlot evening   = pickSlotForTargetHour(todays, zoneOffset, targetDate, 19, "Evening");
            ForecastSlot overnight = pickSlotForTargetHour(todays, zoneOffset, targetDate, 23, "Overnight");

            List<ForecastSlot> slots = new ArrayList<>();
            if (morning != null)   slots.add(morning);
            if (afternoon != null) slots.add(afternoon);
            if (evening != null)   slots.add(evening);
            if (overnight != null) slots.add(overnight);

            // 5) Build domain entity & make advice
            DailyForecast forecast = new DailyForecast(resolvedCity, targetDate, slots);
            String advice = adviceService.makeAdvice(forecast);

            // 6) Convert to OutputData (Presenter will format for UI)
            List<SlotDTO> slotDTOs = new ArrayList<>();
            for (ForecastSlot s : slots) {
                slotDTOs.add(new SlotDTO(
                        s.getLabel(),
                        s.getTemperature(),
                        s.getDescription(),
                        s.getIconCode(),
                        s.getPrecipProbability(),
                        s.getWindSpeed(),
                        s.getFeelsLike()
                ));
            }

            presenter.presentDailyForecast(new DailyForecastOutputData(
                    resolvedCity, slotDTOs, advice, true, "OK"
            ));

        } catch (Exception e) {
            // Any network/parse error — report a friendly message
            presenter.presentDailyForecast(new DailyForecastOutputData(
                    resolvedCity == null ? "" : resolvedCity,
                    new ArrayList<>(),
                    "Failed to fetch forecast. Please check your input or connection.",
                    false,
                    e.getMessage()
            ));
        }
    }

    /**
     * Pick the entry closest to the targetHour among today's entries, and build a ForecastSlot.
     */
    private ForecastSlot pickSlotForTargetHour(List<JSONObject> todays,
                                               ZoneOffset zoneOffset,
                                               LocalDate cityToday,
                                               int targetHour,
                                               String label) {
        // Filter today's entries (already filtered) and find the closest by absolute hour difference
        JSONObject best = todays.stream()
                .min(Comparator.comparingInt(o -> {
                    int h = o.optInt("_localHour", -1);
                    return Math.abs(h - targetHour);
                }))
                .orElse(null);

        if (best == null) return null;

        // Extract fields from the JSON entry
        JSONObject main = best.getJSONObject("main");
        double temp = main.getDouble("temp");
        Double feelsLike = main.has("feels_like") ? main.getDouble("feels_like") : null;

        JSONArray weatherArr = best.getJSONArray("weather");
        JSONObject w0 = weatherArr.getJSONObject(0);
        String desc = w0.getString("description");
        String icon = w0.getString("icon"); // e.g., "10d"

        Double pop = best.has("pop") ? best.getDouble("pop") : null; // 0..1
        Double wind = null;
        if (best.has("wind")) {
            JSONObject windObj = best.getJSONObject("wind");
            if (windObj.has("speed")) wind = windObj.getDouble("speed"); // m/s
        }

        return new ForecastSlot(label, temp, desc, icon, pop, wind, feelsLike);
    }
}