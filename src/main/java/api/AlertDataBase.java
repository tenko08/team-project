package api;

import entities.Alert;

import java.util.List;

/**
 * Data access interface for TTC GTFS-realtime Alerts feed.
 */
public interface AlertDataBase {
    /**
     * Fetch all current alerts from the feed.
     */
    List<Alert> getAllAlerts();

    /**
     * Convenience filter: alerts that affect a specific route.
     */
    List<Alert> getAlertsForRoute(String routeId);

    /**
     * Convenience filter: alerts that affect a specific stop.
     */
    List<Alert> getAlertsForStop(String stopId);
}
