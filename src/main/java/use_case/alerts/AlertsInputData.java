package use_case.alerts;

public class AlertsInputData {
    private final String routeId; // optional
    private final String stopId;  // optional

    public AlertsInputData() {
        this(null, null);
    }

    public AlertsInputData(String routeId, String stopId) {
        this.routeId = routeId;
        this.stopId = stopId;
    }

    public String getRouteId() { return routeId; }
    public String getStopId() { return stopId; }
}
