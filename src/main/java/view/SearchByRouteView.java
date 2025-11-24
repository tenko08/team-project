package view;

import interface_adapter.search_by_route.SearchByRouteController;
import interface_adapter.search_by_route.SearchByRouteViewModel;
import entities.Bus;
import entities.Position;
import entities.Route;

import java.util.List;

public class SearchByRouteView {
    private final SearchByRouteController controller;
    private final SearchByRouteViewModel viewModel;

    public SearchByRouteView(SearchByRouteController controller, SearchByRouteViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;

        viewModel.addPropertyChangeListener(evt -> updateView());
    }

    public void onSearchButtonClicked(String routeNumber) {
        controller.execute(routeNumber);
    }

    public void onRetryButtonClicked() {
        if (viewModel.getRoute() != null) {
            controller.execute(String.valueOf(viewModel.getRoute().getRouteNumber()));
        }
    }

    private void updateView() {
        if (viewModel.isSuccess()) {
            displayBusData(viewModel.getRoute(), viewModel.getBuses());
            
            if (viewModel.isCachedData()) {
                showCachedDataWarning();
            }
        } else {
            showError(viewModel.getErrorMessage());
        }
    }

    private void displayBusData(entities.Route route, List<entities.Bus> buses) {
        if (buses == null || buses.isEmpty() || route == null) {
            System.out.println("No buses found for route " + (route != null ? route.getRouteNumber() : "unknown"));
            return;
        }

        System.out.println("\n=== Route " + route.getRouteNumber() + " - Live Bus Locations ===");
        System.out.println("Found " + buses.size() + " bus(es) on this route\n");

        for (entities.Bus bus : buses) {
            System.out.println("Bus ID: " + bus.getId());
            
            Position position = bus.getPosition();
            if (position != null) {
                System.out.println("  Location: (" + 
                    String.format("%.6f", position.getLatitude()) + ", " + 
                    String.format("%.6f", position.getLongitude()) + ")");
//                System.out.println("  Direction: " + (bus.getDirection() != null ? bus.getDirection() : "Unknown"));
                System.out.println("  Bearing: " + String.format("%.1f", position.getBearing()) + "°");
                System.out.println("  Speed: " + String.format("%.1f", position.getSpeed()) + " m/s");
            } else {
                System.out.println("  Location: Not available");
            }
            
            System.out.println("  Occupancy: " + bus.getOccupancy());
            System.out.println();
        }
    }

    private void showCachedDataWarning() {
        System.out.println("⚠ WARNING: Displaying cached data. API call failed.");
        System.out.println("Click retry to fetch fresh data.\n");
    }

    private void showError(String errorMessage) {
        System.out.println("❌ Error: " + errorMessage);
        
        if (errorMessage != null && errorMessage.contains("Route not found")) {
            System.out.println("Please check the route number and try again.\n");
        }
    }

    public void dispose() {
        viewModel.removePropertyChangeListener(null);
    }
}
