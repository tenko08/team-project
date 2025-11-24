package interface_adapter.alerts;

import entities.Alert;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class AlertsViewModel {
    private final String viewName = "alerts";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private boolean loading;
    private boolean success;
    private String errorMessage;
    private List<Alert> alerts = new ArrayList<>();
    private String selectedRouteId;
    private String selectedStopId;

    public String getViewName() { return viewName; }

    public boolean isLoading() { return loading; }
    public void setLoading(boolean loading) { this.loading = loading; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public List<Alert> getAlerts() { return alerts; }
    public void setAlerts(List<Alert> alerts) { this.alerts = alerts == null ? new ArrayList<>() : alerts; }

    public String getSelectedRouteId() { return selectedRouteId; }
    public void setSelectedRouteId(String selectedRouteId) { this.selectedRouteId = selectedRouteId; }

    public String getSelectedStopId() { return selectedStopId; }
    public void setSelectedStopId(String selectedStopId) { this.selectedStopId = selectedStopId; }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
