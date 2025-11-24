package use_case.alerts;

import api.AlertDataBase;
import entities.Alert;

import java.util.List;

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
            List<Alert> alerts = alertDataBase.getAllAlerts();
            boolean success = alerts != null;
            presenter.present(new AlertsOutputData(
                    success,
                    success ? null : "Failed to load alerts",
                    alerts,
                    inputData == null ? null : inputData.getRouteId(),
                    inputData == null ? null : inputData.getStopId()
            ));
        } catch (Exception e) {
            presenter.present(new AlertsOutputData(false, e.getMessage(), List.of()));
        }
    }
}
