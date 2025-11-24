package use_case.search_by_route;

import entities.Bus;
import entities.Route;
import interface_adapter.search_by_route.SearchByRouteGateway;

import java.util.List;
import java.util.Map;

public class SearchByRouteInteractor implements SearchByRouteInputBoundary {
    private final SearchByRouteGateway searchByRouteGateway;
    private final SearchByRouteOutputBoundary outputBoundary;

    public SearchByRouteInteractor(SearchByRouteGateway searchByRouteGateway,
                                   SearchByRouteOutputBoundary outputBoundary) {
        this.searchByRouteGateway = searchByRouteGateway;
        this.outputBoundary = outputBoundary;
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
            } else {
                String errorMessage = (String) result.getOrDefault("message", "Route not found");
                outputBoundary.prepareFailView(errorMessage);
            }

        } catch (Exception e) {
            outputBoundary.prepareFailView("System error: " + e.getMessage());
        }
    }
}
