package view;

import interface_adapter.search_by_route.SearchByRouteController;
import interface_adapter.search_by_route.SearchByRouteViewModel;
import entities.*;

import java.util.List;

public class SearchByRouteView {
    private final SearchByRouteController controller;
    private final SearchByRouteViewModel viewModel;

    public SearchByRouteView(SearchByRouteController controller, SearchByRouteViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;

        viewModel.addPropertyChangeListener(evt -> {updateView();});
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
            } else {
                showError(viewModel.getErrorMessage());
            }
        }
    }

    private void displayBusData(Route route, List<Bus> buses) {
        if (buses == null || buses.isEmpty() || route == null) {
            System.out.println("No buses found");
            return;
        }

        System.out.println("Buses found");

        for (Bus bus : buses) {
            System.out.println("Bus ID: " + bus.getId());

            Position position = bus.getPosition();
            if (position != null) {
                System.out.println("Position: " + position);
            } else {
                System.out.println("Position is null");
            }
            System.out.println("Occupancy:" + bus.getOccupancy());
        }

    }

    private void showCachedDataWarning() {
        System.out.println("failed");
    }

    private void showError(String errorMessage) {
        System.out.println(errorMessage);
    }




}
