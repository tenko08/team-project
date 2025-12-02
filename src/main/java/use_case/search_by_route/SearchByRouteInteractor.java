package use_case.search_by_route;

import entities.Bus;
import entities.Route;
import interface_adapter.search_by_route.SearchByRouteGateway;
import use_case.map.MapInputBoundary;
import use_case.map.RouteShapeDataAccessInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchByRouteInteractor implements SearchByRouteInputBoundary {
    private final SearchByRouteGateway searchByRouteGateway;
    private final SearchByRouteOutputBoundary outputBoundary;
    private final RouteShapeDataAccessInterface routeShapeDataAccess;
    private final MapInputBoundary mapInputBoundary;

    public SearchByRouteInteractor(SearchByRouteGateway searchByRouteGateway,
                                   SearchByRouteOutputBoundary outputBoundary,
                                   RouteShapeDataAccessInterface routeShapeDataAccessInterface,
                                   MapInputBoundary mapInputBoundary) {
        this.searchByRouteGateway = searchByRouteGateway;
        this.outputBoundary = outputBoundary;
        this.routeShapeDataAccess = routeShapeDataAccessInterface;
        this.mapInputBoundary = mapInputBoundary;
    }

    @Override
    public void execute(SearchByRouteInputData inputData) {
        String routeNumber = inputData.getRouteNumber();

        if (routeNumber == null || routeNumber.trim().isEmpty()) {
            outputBoundary.prepareFailView("Route number cannot be empty");
            return;
        }

        try {
            Map<String, Object> result = searchByRouteGateway.getBusesByRoute(routeNumber);

            boolean success = (Boolean) result.getOrDefault("success", false);
            boolean isCached = (Boolean) result.getOrDefault("cached", false);

            if (success) {
                Route route = (Route) result.get("route");
                @SuppressWarnings("unchecked")
                List<Bus> buses = (List<Bus>) result.get("buses");

                SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                        true,
                        route,
                        buses,
                        null,
                        isCached
                );

                if (isCached) {
                    outputBoundary.prepareCachedView(outputData);
                } else {
                    outputBoundary.prepareSuccessView(outputData);
                }
                mapInputBoundary.showRoute(outputData);
            } else {
                int id = Integer.parseInt(routeNumber);
                if (routeShapeDataAccess.hasRoute(id)) {
                    SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                            false,
                            new Route(id),
                            new ArrayList(),
                            null,
                            isCached
                    );
                    mapInputBoundary.showRoute(outputData);
                    outputBoundary.prepareFailView("No buses running at this time.");
                }
                else {
                    String errorMessage = (String) result.getOrDefault("message", "Route not found");
                    outputBoundary.prepareFailView(errorMessage);
                }
            }

        } catch (Exception e) {
            outputBoundary.prepareFailView("System error: " + e.getMessage());
        }
    }
}
