package use_case.bus_schedule_eta;

public class BusScheduleInputData {
    private final String stopId;
    private final String routeId; // 可选，用于特定路线的时刻表

    public BusScheduleInputData(String stopId) {
        this.stopId = stopId;
        this.routeId = null;
    }

    public BusScheduleInputData(String stopId, String routeId) {
        this.stopId = stopId;
        this.routeId = routeId;
    }

    public String getStopId() {
        return stopId;
    }

    public String getRouteId() {
        return routeId;
    }

    public boolean hasRouteId() {
        return routeId != null;
    }
}
