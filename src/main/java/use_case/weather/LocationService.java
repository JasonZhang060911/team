package use_case.weather;

/**
 * Abstract location service to get current city name.
 */
public interface LocationService {
    String getCurrentCity() throws Exception;
}
