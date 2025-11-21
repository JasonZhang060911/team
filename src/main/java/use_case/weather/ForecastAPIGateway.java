package use_case.weather;

/**
 * Gateway abstraction to fetch "5 day / 3 hour" forecast JSON.
 */
public interface ForecastAPIGateway {
    /**
     * Request 5-day/3-hour forecast JSON by city name.
     * @param cityName city name (already decided; could be auto-detected beforehand)
     * @return raw JSON string from API
     * @throws Exception for network or HTTP errors
     */
    String request3hForecastJson(String cityName) throws Exception;
}
