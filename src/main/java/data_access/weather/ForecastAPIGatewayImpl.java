package data_access.weather;

import use_case.weather.ForecastAPIGateway;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * ForecastAPIGatewayImpl: calls OpenWeatherMap 5-day/3-hour forecast API.
 * Returns raw JSON string if HTTP 200; otherwise throws an Exception with error body.
 */
public class ForecastAPIGatewayImpl implements ForecastAPIGateway {

    private static final String BASE =
            "https://api.openweathermap.org/data/2.5/forecast?appid="
                    + WeatherAPIConfig.API_KEY + "&units=metric&q=";

    @Override
    public String request3hForecastJson(String cityName) throws Exception {
        String encoded = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String urlStr = BASE + encoded;

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(10_000);

        int code = conn.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();

        String body;
        try (Scanner sc = new Scanner(is, StandardCharsets.UTF_8)) {
            sc.useDelimiter("\\A");
            body = sc.hasNext() ? sc.next() : "";
        }

        if (code >= 200 && code < 300) {
            return body;
        } else {
            throw new Exception("HTTP " + code + ": " + body);
        }
    }
}