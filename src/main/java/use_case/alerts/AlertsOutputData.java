package use_case.alerts;

import entities.Alert;

import java.util.ArrayList;
import java.util.List;

public class AlertsOutputData {
    private final boolean success;
    private final String errorMessage;
    private final List<Alert> alerts;
    private final String selectedRouteId;
    private final String selectedStopId;

    public AlertsOutputData(boolean success, String errorMessage, List<Alert> alerts) {
        this(success, errorMessage, alerts, null, null);
    }

    public AlertsOutputData(boolean success, String errorMessage, List<Alert> alerts,
                            String selectedRouteId, String selectedStopId) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.alerts = alerts == null ? new ArrayList<>() : alerts;
        this.selectedRouteId = selectedRouteId;
        this.selectedStopId = selectedStopId;
    }

    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    public List<Alert> getAlerts() { return alerts; }
    public String getSelectedRouteId() { return selectedRouteId; }
    public String getSelectedStopId() { return selectedStopId; }
}
