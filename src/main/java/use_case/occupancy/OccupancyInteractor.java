package use_case.occupancy;
import entities.Bus;

public class OccupancyInteractor implements OccupancyInputBoundary {
    private final OccupancyOutputBoundary occupancyOutputBoundary;
    private final OccupancyDataAccessInterface dataAccessInterface;

    public OccupancyInteractor(OccupancyOutputBoundary occupancyOutputBoundary,
            OccupancyDataAccessInterface dataAccessInterface) {
        this.occupancyOutputBoundary = occupancyOutputBoundary;
        this.dataAccessInterface = dataAccessInterface;
    }

    @Override
    public void execute(OccupancyInputData occupancyInputData) {
        // Create a temporary Bus object with the ID to pass to the DAO.
        // We assume the DAO only needs the ID to look up the occupancy.
        Bus bus = new Bus(occupancyInputData.getBusId(), null, null);

        String occupancy = dataAccessInterface.getOccupancy(bus);

        if (occupancy == null) {
            occupancyOutputBoundary.prepareFailView("No data available");
        } else {
            occupancyOutputBoundary.prepareSuccessView(new OccupancyOutputData(occupancy));
        }
    }
}
