package interface_adapter.search_by_route;

import entities.Bus;
import entities.Route;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class SearchByRouteViewModel {
    private boolean success;
    private Route route;
    private List<Bus> buses;
    private boolean isCachedData;
    private String errorMessage;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public List<Bus> getBuses() {
        return buses;
    }

    public void setBuses(List<Bus> buses) {
        this.buses = buses;
    }

    public boolean isCachedData() {
        return isCachedData;
    }

    public void setCachedData(boolean cachedData) {
        this.isCachedData = cachedData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

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
