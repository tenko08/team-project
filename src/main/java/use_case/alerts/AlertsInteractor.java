package use_case.alerts;

import api.AlertDataBase;
import entities.Alert;
import use_case.search_by_route.SearchByRouteInputBoundary;
import use_case.search_by_route.SearchByRouteInputData;

import java.util.List;
import java.util.stream.Collectors;

public class AlertsInteractor implements AlertsInputBoundary {
    private final AlertDataBase alertDataBase;
    private final AlertsOutputBoundary presenter;
    private final SearchByRouteInputBoundary searchByRouteInputBoundary;

    public AlertsInteractor(AlertDataBase alertDataBase, AlertsOutputBoundary presenter) {
        this.alertDataBase = alertDataBase;
        this.presenter = presenter;
        this.searchByRouteInputBoundary = null;
    }

    public AlertsInteractor(AlertDataBase alertDataBase, AlertsOutputBoundary presenter,
                            SearchByRouteInputBoundary searchByRouteInputBoundary) {
        this.alertDataBase = alertDataBase;
        this.presenter = presenter;
        this.searchByRouteInputBoundary = searchByRouteInputBoundary;
    }

    @Override
    public void execute(AlertsInputData inputData) {
        // Derive inputs up-front to avoid NPEs
        final String routeId = (inputData == null) ? null : inputData.getRouteId();
        final String stopId = (inputData == null) ? null : inputData.getStopId();

        try {
            // Always fetch from the datasource once, then filter locally so tests that
            // simulate exceptions/nulls on getAllAlerts still behave as expected.
            List<Alert> alerts = alertDataBase.getAllAlerts();

            if (alerts != null) {
                // Apply filters if provided
                if (routeId != null && !routeId.isBlank()) {
                    alerts = alerts.stream()
                            .filter(a -> a.getRouteIds().contains(routeId))
                            .collect(Collectors.toList());
                }
                if (stopId != null && !stopId.isBlank()) {
                    alerts = alerts.stream()
                            .filter(a -> a.getStopIds().contains(stopId))
                            .collect(Collectors.toList());
                }
            }

            boolean success = alerts != null;
            presenter.present(new AlertsOutputData(
                    success,
                    success ? null : "Failed to load alerts",
                    alerts,
                    routeId,
                    stopId
            ));
        } catch (Exception e) {
            // Ensure failure does not produce null alerts list
            presenter.present(new AlertsOutputData(false, e.getMessage(), List.of(), routeId, stopId));
            return; // Do not proceed to any optional calls in error case
        }

        // Call optional collaborator without affecting the primary result
        if (searchByRouteInputBoundary != null && routeId != null && !routeId.isBlank()) {
            try {
                searchByRouteInputBoundary.execute(new SearchByRouteInputData(routeId));
            } catch (Exception ignored) {
                // Intentionally ignore to avoid overriding already-presented output
            }
        }
    }
}
