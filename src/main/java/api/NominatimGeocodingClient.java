package api;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal client for Nominatim geocoding (OpenStreetMap) to convert an address string
 * into latitude/longitude. Uses the public /search endpoint.
 */
public class NominatimGeocodingClient {
    private final OkHttpClient http;
    private final String baseUrl;
    private final String userAgent;

    public NominatimGeocodingClient() {
        this(defaultClient(), "https://nominatim.openstreetmap.org", "TTC Map Viewer/1.0");
    }

    public NominatimGeocodingClient(OkHttpClient http, String baseUrl, String userAgent) {
        this.http = http;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.userAgent = userAgent;
    }

    private static OkHttpClient defaultClient() {
        return new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(15))
                .build();
    }

    public List<Result> search(String query) throws IOException {
        return search(query, 5);
    }

    public List<Result> search(String query, int limit) throws IOException {
        HttpUrl url = HttpUrl.parse(baseUrl + "/search").newBuilder()
                .addQueryParameter("format", "jsonv2")
                .addQueryParameter("q", query)
                .addQueryParameter("limit", String.valueOf(Math.max(1, Math.min(limit, 10))))
                .addQueryParameter("addressdetails", "1")
                .build();

        Request req = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .get()
                .build();

        try (Response resp = http.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                throw new IOException("Geocoding failed: HTTP " + resp.code());
            }
            String body = resp.body() != null ? resp.body().string() : "[]";
            return parseResults(body);
        }
    }

    // Visible for tests
    static List<Result> parseResults(String json) {
        List<Result> out = new ArrayList<>();
        JSONArray arr = new JSONArray(json);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            String display = o.optString("display_name", "");
            String latStr = o.optString("lat", null);
            String lonStr = o.optString("lon", null);
            if (latStr == null || lonStr == null) continue;
            try {
                double lat = Double.parseDouble(latStr);
                double lon = Double.parseDouble(lonStr);
                out.add(new Result(display, lat, lon));
            } catch (NumberFormatException ignore) {
                // skip invalid entries
            }
        }
        return out;
    }

    public static class Result {
        private final String displayName;
        private final double latitude;
        private final double longitude;

        public Result(String displayName, double latitude, double longitude) {
            this.displayName = displayName;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getDisplayName() { return displayName; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }

        @Override
        public String toString() {
            return displayName + " (" + latitude + ", " + longitude + ")";
        }
    }
}
