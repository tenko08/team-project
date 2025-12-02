package use_case.occupancy;

import api.BusDataBase;
import entities.Bus;
import use_case.search_by_route.SearchByRouteInputBoundary;
import use_case.search_by_route.SearchByRouteInputData;

/**
 * Interactor for the Occupancy Use Case.
 * Converts bus occupancy status from API format to user-friendly format.
 */
public class OccupancyInteractor implements OccupancyInputBoundary {
    private final BusDataBase busDataBase;
    private final OccupancyOutputBoundary outputBoundary;
    private final SearchByRouteInputBoundary searchByRouteInputBoundary;

    public OccupancyInteractor(BusDataBase busDataBase, OccupancyOutputBoundary outputBoundary) {
        this.busDataBase = busDataBase;
        this.outputBoundary = outputBoundary;
        this.searchByRouteInputBoundary = null;
    }

    public OccupancyInteractor(BusDataBase busDataBase, OccupancyOutputBoundary outputBoundary,
                               SearchByRouteInputBoundary searchByRouteInputBoundary) {
        this.busDataBase = busDataBase;
        this.outputBoundary = outputBoundary;
        this.searchByRouteInputBoundary = searchByRouteInputBoundary;
    }

    @Override
    public void execute(OccupancyInputData inputData) {
        if (inputData.getBusId() != -1) {
            // Single bus case
            handleSingleBus(inputData.getBusId(), inputData.getRoute());
        } else if (inputData.getRoute() != null) {
            // Route case
            handleRoute(inputData.getRoute());
        } else {
            outputBoundary.prepareFailView("Invalid input data");
        }
    }

    private void handleSingleBus(int busId, entities.Route route) {
        // Try to get the bus from the route's trips first
        Bus bus = null;
        if (route != null && route.getBusList() != null) {
            for (Bus b : route.getBusList()) {
                if (b.getId() == busId) {
                    bus = b;
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
        OccupancyOutputData outputData = new OccupancyOutputData(busId, occupancyLevel, route);
        outputBoundary.prepareSuccessView(outputData);
    }

    private void handleRoute(entities.Route route) {
        int routeNumber = route.getRouteNumber();
        java.util.List<Bus> buses = busDataBase.getBusesByRouteId(routeNumber);
        searchByRouteInputBoundary.execute(new SearchByRouteInputData(String.valueOf(routeNumber)));

        if (buses.isEmpty()) {
            outputBoundary.prepareFailView("No buses found for route " + route.getRouteNumber());
            return;
        }

        java.util.Map<Integer, String> busOccupancies = new java.util.HashMap<>();
        for (Bus bus : buses) {
            String occupancy = convertOccupancy(bus.getOccupancy());
            if (occupancy != null) {
                busOccupancies.put(bus.getId(), occupancy);
            } else {
                busOccupancies.put(bus.getId(), "Unknown");
            }
        }

        OccupancyOutputData outputData = new OccupancyOutputData(busOccupancies, route);
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
