package api;

import com.google.transit.realtime.GtfsRealtime;
import entities.Alert;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AlertDataBaseAPI implements AlertDataBase {
    private static final String API_URL = "https://bustime.ttc.ca/gtfsrt/alerts";
    // Alerts are associated with routes and/or stops
    @Override
    public List<Alert> getAllAlerts() {
        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder().url(API_URL).build();

        List<Alert> alerts = new ArrayList<>();

        try {
            final Response response = client.newCall(request).execute();
            if (response.body() == null) return alerts;
            final byte[] bytes = response.body().bytes();

            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(bytes);

            long feedTimestamp = feed.hasHeader() && feed.getHeader().hasTimestamp()
                    ? feed.getHeader().getTimestamp()
                    : 0L;

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                if (!entity.hasAlert()) continue;

                GtfsRealtime.Alert a = entity.getAlert();

                // Choose English translation if available, else first available.
                String header = extractTranslatedText(a.getHeaderText());
                String description = extractTranslatedText(a.getDescriptionText());

                String cause = a.hasCause() ? a.getCause().name() : "UNKNOWN_CAUSE";
                String effect = a.hasEffect() ? a.getEffect().name() : "UNKNOWN_EFFECT";

                Set<String> routeIds = new HashSet<>();
                Set<String> stopIds = new HashSet<>();
                for (GtfsRealtime.EntitySelector ie : a.getInformedEntityList()) {
                    if (ie.hasRouteId()) routeIds.add(ie.getRouteId());
                    if (ie.hasStopId()) stopIds.add(ie.getStopId());
                }

                Alert alert = new Alert(
                        entity.getId(),
                        header,
                        description,
                        cause,
                        effect,
                        feedTimestamp,
                        new ArrayList<>(routeIds),
                        new ArrayList<>(stopIds)
                );
                alerts.add(alert);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return alerts;
    }

    @Override
    public List<Alert> getAlertsForRoute(String routeId) {
        List<Alert> all = getAllAlerts();
        List<Alert> result = new ArrayList<>();
        for (Alert a : all) {
            if (a.getRouteIds().contains(routeId)) result.add(a);
        }
        return result;
    }

    @Override
    public List<Alert> getAlertsForStop(String stopId) {
        List<Alert> all = getAllAlerts();
        List<Alert> result = new ArrayList<>();
        for (Alert a : all) {
            if (a.getStopIds().contains(stopId)) result.add(a);
        }
        return result;
    }

    public static void main(String[] args) {
        List<Alert> alerts = new AlertDataBaseAPI().getAllAlerts();
        System.out.println(alerts);

    }

    private static String extractTranslatedText(GtfsRealtime.TranslatedString ts) {
        if (ts == null) return "";
        String first = "";
        for (GtfsRealtime.TranslatedString.Translation tr : ts.getTranslationList()) {
            if (first.isEmpty()) first = tr.getText();
            if (tr.hasLanguage() && "en".equalsIgnoreCase(tr.getLanguage())) {
                return tr.getText();
            }
        }
        return first;
    }
}
