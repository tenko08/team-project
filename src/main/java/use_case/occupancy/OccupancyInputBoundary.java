package use_case.occupancy;

public interface OccupancyInputBoundary {

    /**
     * Executes the occupancy use case.
     * @param occupancyInputData the occupancy input data
     */
    void execute(OccupancyInputData occupancyInputData);
}
