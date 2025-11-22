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
     * Executes the Occupancy Use Case.
     * 
     * @param busId the ID of the bus to check occupancy for
     * @param route the route the bus is on (optional/contextual)
     */
    public void execute(int busId, Route route) {
        OccupancyInputData occupancyInputData = new OccupancyInputData(busId, route);
        occupancyUseCaseInteractor.execute(occupancyInputData);
    }
}
