package interface_adapter.alerts;

import use_case.alerts.AlertsOutputBoundary;
import use_case.alerts.AlertsOutputData;

public class AlertsPresenter implements AlertsOutputBoundary {
    private final AlertsViewModel viewModel;

    public AlertsPresenter(AlertsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(AlertsOutputData outputData) {
        viewModel.setLoading(false);
        viewModel.setSuccess(outputData.isSuccess());
        viewModel.setErrorMessage(outputData.getErrorMessage());
        viewModel.setAlerts(outputData.getAlerts());
        viewModel.setSelectedRouteId(outputData.getSelectedRouteId());
        viewModel.setSelectedStopId(outputData.getSelectedStopId());
        viewModel.firePropertyChanged();
    }
}
