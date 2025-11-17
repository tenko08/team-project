package use_case.occupancy;

public class OccupancyInteractor implements OccupancyInputBoundary{
    private final OccupancyOutputBoundary occupancyOutputBoundary;
    private final OccupancyDataAccessInterface dataAccessInterface;

    public OccupancyInteractor(OccupancyInputData occupancyInputData, OccupancyDataAccessInterface dataAccessInterface) {
        this.occupancyOutputBoundary = occupancyOutputBoundary;
        this.dataAccessInterface = dataAccessInterface;
    }

    @Override
    public void execute(OccupancyInputData occupancyInputData) {
    }
}
