package use_case.occupancy;

import entities.Route;

/**
 * The Input Data for the Occupancy use case.
 */
public class OccupancyInputData {
    private final Route route;

    public OccupancyInputData(Route route) {
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }
}
