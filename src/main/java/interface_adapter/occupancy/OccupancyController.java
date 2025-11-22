package interface_adapter.occupancy;

import entities.Route;
import use_case.occupancy.OccupancyInputBoundary;
import use_case.occupancy.OccupancyInputData;

public class OccupancyController {
    private final OccupancyInputBoundary occupancyUseCaseInteractor;

    public OccupancyController(OccupancyInputBoundary occupancyUseCaseInteractor) {
        this.occupancyUseCaseInteractor = occupancyUseCaseInteractor;
    }

    /**
     * Executes the Occupancy Use Case for a single bus.
     * 
     * @param busId the ID of the bus to check occupancy for
     * @param route the route the bus is on (optional/contextual)
     */
    public void execute(int busId, Route route) {
        OccupancyInputData occupancyInputData = new OccupancyInputData(busId, route);
        occupancyUseCaseInteractor.execute(occupancyInputData);
    }

    /**
     * Executes the Occupancy Use Case for all buses on a route.
     * 
     * @param route the route to check occupancy for
     */
    public void execute(Route route) {
        if (route != null && route.getTripList() != null) {
            for (entities.Trip trip : route.getTripList()) {
                if (trip.getBus() != null) {
                    execute(trip.getBus().getId(), route);
                }
            }
        }
    }
}
