package use_case.occupancy;

import entities.Route;

public class OccupancyInputData {
    private final int busId;
    private final Route route;

    public OccupancyInputData(int busId, Route route) {
        this.busId = busId;
        this.route = route;
    }

    public OccupancyInputData(Route route) {
        this.busId = -1; // Indicates no specific bus
        this.route = route;
    }

    public int getBusId() {
        return busId;
    }

    public Route getRoute() {
        return route;
    }
}
