package data_access.weather;

import org.json.JSONObject;
import use_case.weather.LocationService;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * LocationServiceImpl: gets current city via IP-based API (ip-api.com).
 * This is a simple free service, good enough for desktop demos.
 */
public class LocationServiceImpl implements LocationService {

    private static final String API = "http://ip-api.com/json";

    @Override
    public String getCurrentCity() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(API).openConnection();
        conn.setRequestMethod("GET");
        int code = conn.getResponseCode();

        InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        String body;
        try (Scanner sc = new Scanner(is, "UTF-8")) {
            sc.useDelimiter("\\A");
            body = sc.hasNext() ? sc.next() : "";
        }

        if (code >= 200 && code < 300) {
            JSONObject obj = new JSONObject(body);
            if ("success".equalsIgnoreCase(obj.optString("status"))) {
                String city = obj.optString("city", "");
                if (city == null || city.isEmpty()) throw new Exception("City not available.");
                return city;
            }
            throw new Exception("Location failed: " + obj.toString());
        } else {
            throw new Exception("HTTP " + code + ": " + body);
        }
    }
}
