package use_case.occupancy;
import api.BusDataBase;
import entities.Bus;

/**
 * Interactor for the Occupancy Use Case.
 * Converts bus occupancy status from API format to user-friendly format.
 */
public class OccupancyInteractor implements OccupancyInputBoundary {
    private final BusDataBase busDataBase;
    private final OccupancyOutputBoundary outputBoundary;

    public OccupancyInteractor(BusDataBase busDataBase, OccupancyOutputBoundary outputBoundary) {
        this.busDataBase = busDataBase;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(OccupancyInputData inputData) {
        int busId = inputData.getBusId();
        
        // Try to get the bus from the route's trips first
        Bus bus = null;
        if (inputData.getRoute() != null && inputData.getRoute().getTripList() != null) {
            for (entities.Trip trip : inputData.getRoute().getTripList()) {
                if (trip.getBus() != null && trip.getBus().getId() == busId) {
                    bus = trip.getBus();
                    break;
                }
            }
        }
        
        // If bus not found in route, fetch it from the API
        if (bus == null) {
            bus = busDataBase.getBus(busId);
        }
        
        if (bus == null) {
            outputBoundary.prepareFailView("Bus with ID " + busId + " not found");
            return;
        }
        
        // Get the occupancy status from the bus
        String apiOccupancy = bus.getOccupancy();
        
        if (apiOccupancy == null || apiOccupancy.isEmpty() || apiOccupancy.equals("UNKNOWN")) {
            outputBoundary.prepareFailView("No occupancy data available for bus " + busId);
            return;
        }
        
        // Convert API occupancy format to user-friendly format
        String occupancyLevel = convertOccupancy(apiOccupancy);
        
        if (occupancyLevel == null) {
            outputBoundary.prepareFailView("Unknown occupancy status: " + apiOccupancy);
            return;
        }
        
        // Prepare success view with converted occupancy level
        OccupancyOutputData outputData = new OccupancyOutputData(busId, occupancyLevel);
        outputBoundary.prepareSuccessView(outputData);
    }
    
    /**
     * Converts API occupancy status to user-friendly format.
     * 
     * @param apiOccupancy the occupancy status from the API
     * @return "Empty", "Full", or "Almost Full", or null if unknown
     */
    private String convertOccupancy(String apiOccupancy) {
        if (apiOccupancy == null) {
            return null;
        }
        
        // Handle different possible formats from the API
        String upperOccupancy = apiOccupancy.toUpperCase().trim();
        
        switch (upperOccupancy) {
            case "EMPTY":
                return "Empty";
            case "FULL":
            case "MANY_SEATS_AVAILABLE":
                return "Full";
            case "FEW_SEATS_AVAILABLE":
                return "Almost Full";
            default:
                return null;
        }
    }
}
