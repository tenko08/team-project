package use_case.occupancy;

import entities.Route;

/**
 * The Input Data for the Occupancy use case.
 */
public class OccupancyInputData {
    private final Route route;
    private final int busId;

    public OccupancyInputData(int busId, Route route) {
        this.busId = busId;
        this.route = route;
    }

    public int getBusId() {
        return busId;
    }

    public Route getRoute() {
        return route;
    }
}
