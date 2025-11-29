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
        this(defaultClient(), "https://nominatim.openstreetmap.org", buildDefaultUserAgent());
    }

    public NominatimGeocodingClient(OkHttpClient http, String baseUrl, String userAgent) {
        this.http = http;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.userAgent = userAgent;
    }

    private static OkHttpClient defaultClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(25))
                .writeTimeout(Duration.ofSeconds(25))
                .retryOnConnectionFailure(true)
                .build();
    }

    private static String buildDefaultUserAgent() {
        // Nominatim usage policy requires a valid identifying User-Agent with a way to contact you.
        // You can override the email via env NOMINATIM_EMAIL or system property nominatim.email
        String email = getContactEmail();
        if (email == null || email.isBlank()) {
            return "TTC Map Viewer/1.0 (no-contact)";
        }
        return "TTC Map Viewer/1.0 (contact: " + email + ")";
    }

    private static String getContactEmail() {
        String email = System.getProperty("nominatim.email");
        if (email == null || email.isBlank()) {
            try {
                String env = System.getenv("NOMINATIM_EMAIL");
                if (env != null && !env.isBlank()) email = env;
            } catch (SecurityException ignored) {}
        }
        return email;
    }

    public List<Result> search(String query) throws IOException {
        return search(query, 5);
    }

    public List<Result> search(String query, int limit) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/search").newBuilder()
                .addQueryParameter("format", "jsonv2")
                .addQueryParameter("q", query)
                .addQueryParameter("limit", String.valueOf(Math.max(1, Math.min(limit, 10))))
                .addQueryParameter("addressdetails", "1")
                ;

        // If we have a contact email, include it per Nominatim recommendations
        String contactEmail = getContactEmail();
        if (contactEmail != null && !contactEmail.isBlank()) {
            urlBuilder.addQueryParameter("email", contactEmail);
        }
        HttpUrl url = urlBuilder.build();

        Request req = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept-Language", "en")
                .get()
                .build();

        IOException lastEx = null;
        // Simple retry: up to 2 attempts for timeouts, 429 and temporary 5xx (503/502/504)
        for (int attempt = 1; attempt <= 2; attempt++) {
            try (Response resp = http.newCall(req).execute()) {
                if (resp.code() == 429) {
                    // Too Many Requests — back off and retry once
                    if (attempt == 2) {
                        throw new IOException("Geocoding rate limited (HTTP 429). Please try again later.");
                    }
                    try { Thread.sleep(750); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
                    continue;
                }
                if (resp.code() == 503 || resp.code() == 502 || resp.code() == 504) {
                    // Service temporarily unavailable/bad gateway/gateway timeout — back off and retry once
                    if (attempt == 2) {
                        throw new IOException("Geocoding service temporarily unavailable (HTTP " + resp.code() + "). Please try again later.");
                    }
                    long delayMs = 1000;
                    String retryAfter = resp.header("Retry-After");
                    if (retryAfter != null) {
                        try {
                            // Retry-After may be seconds; parse conservatively
                            long seconds = Long.parseLong(retryAfter.trim());
                            delayMs = Math.max(500, Math.min(5000, seconds * 1000));
                        } catch (NumberFormatException ignored) {}
                    }
                    try { Thread.sleep(delayMs); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
                    continue;
                }
                if (!resp.isSuccessful()) {
                    throw new IOException("Geocoding failed: HTTP " + resp.code());
                }
                String body = resp.body() != null ? resp.body().string() : "[]";
                return parseResults(body);
            } catch (java.net.SocketTimeoutException ste) {
                lastEx = new IOException("Geocoding request timed out. Please check your internet connection and try again.", ste);
                if (attempt == 2) throw lastEx;
                try { Thread.sleep(500); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            } catch (IOException ioe) {
                lastEx = ioe;
                // Non-timeout IO errors: don't retry unless it's the first attempt and potentially transient
                if (attempt == 2) throw ioe;
                try { Thread.sleep(300); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }
        // Should not reach here
        if (lastEx != null) throw lastEx;
        throw new IOException("Unknown geocoding error");
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
