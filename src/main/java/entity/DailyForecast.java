package entity;

import java.time.LocalDate;
import java.util.List;

/**
 * DailyForecast: today's forecast for a city, composed of multiple slots.
 * Pure domain object.
 */
public class DailyForecast {
    private final String city;
    private final LocalDate date;        // local date of the city
    private final List<ForecastSlot> slots;

    public DailyForecast(String city, LocalDate date, List<ForecastSlot> slots) {
        this.city = city;
        this.date = date;
        this.slots = slots;
    }

    public String getCity() { return city; }
    public LocalDate getDate() { return date; }
    public List<ForecastSlot> getSlots() { return slots; }
}