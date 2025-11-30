package use_case.alerts;

import api.AlertDataBase;
import entities.Alert;

import java.util.List;
import java.util.stream.Collectors;

public class AlertsInteractor implements AlertsInputBoundary {
    private final AlertDataBase alertDataBase;
    private final AlertsOutputBoundary presenter;

    public AlertsInteractor(AlertDataBase alertDataBase, AlertsOutputBoundary presenter) {
        this.alertDataBase = alertDataBase;
        this.presenter = presenter;
    }

    @Override
    public void execute(AlertsInputData inputData) {
        try {
            // Always fetch from the datasource once, then filter locally so tests that
            // simulate exceptions/nulls on getAllAlerts still behave as expected.
            List<Alert> alerts = alertDataBase.getAllAlerts();
            String routeId = (inputData == null) ? null : inputData.getRouteId();
            String stopId = (inputData == null) ? null : inputData.getStopId();

            if (alerts != null) {
                // Apply filters if provided
                if (routeId != null && !routeId.isBlank()) {
                    alerts = alerts.stream()
                            .filter(a -> a.getRouteIds() != null && a.getRouteIds().contains(routeId))
                            .collect(Collectors.toList());
                }
                if (stopId != null && !stopId.isBlank()) {
                    alerts = alerts.stream()
                            .filter(a -> a.getStopIds() != null && a.getStopIds().contains(stopId))
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
            presenter.present(new AlertsOutputData(false, e.getMessage(), List.of()));
        }
    }
}
