package interface_adapter.alerts;

import use_case.alerts.AlertsInputBoundary;
import use_case.alerts.AlertsInputData;

public class AlertsController {
    private final AlertsInputBoundary interactor;

    public AlertsController(AlertsInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute() {
        interactor.execute(new AlertsInputData());
    }

    public void executeForRoute(String routeId) {
        interactor.execute(new AlertsInputData(routeId, null));
    }

    public void executeForStop(String stopId) {
        interactor.execute(new AlertsInputData(null, stopId));
    }

    public void executeFor(String routeId, String stopId) {
        interactor.execute(new AlertsInputData(routeId, stopId));
    }
}
