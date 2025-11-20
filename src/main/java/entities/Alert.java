package entities;

import java.util.Collections;
import java.util.List;

/**
 * Simple domain model for a TTC GTFS-realtime Alert.
 */
public class Alert {
    private final String id; // FeedEntity id
    private final String headerText;
    private final String descriptionText;
    private final String cause;
    private final String effect;
    private final long timestamp; // feed header timestamp (epoch seconds) when fetched
    private final List<String> routeIds; // informed_entity route_ids
    private final List<String> stopIds;  // informed_entity stop_ids

    public Alert(String id,
                 String headerText,
                 String descriptionText,
                 String cause,
                 String effect,
                 long timestamp,
                 List<String> routeIds,
                 List<String> stopIds) {
        this.id = id;
        this.headerText = headerText;
        this.descriptionText = descriptionText;
        this.cause = cause;
        this.effect = effect;
        this.timestamp = timestamp;
        this.routeIds = routeIds == null ? List.of() : List.copyOf(routeIds);
        this.stopIds = stopIds == null ? List.of() : List.copyOf(stopIds);
    }

    public String getId() {
        return id;
    }

    public String getHeaderText() {
        return headerText;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public String getCause() {
        return cause;
    }

    public String getEffect() {
        return effect;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<String> getRouteIds() {
        return Collections.unmodifiableList(routeIds);
    }

    public List<String> getStopIds() {
        return Collections.unmodifiableList(stopIds);
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id='" + id + '\'' +
                ", header='" + headerText + '\'' +
                ", cause='" + cause + '\'' +
                ", effect='" + effect + '\'' +
                ", routes=" + routeIds +
                ", stops=" + stopIds +
                '}';
    }
}
