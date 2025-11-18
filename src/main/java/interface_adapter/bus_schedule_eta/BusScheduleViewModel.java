package interface_adapter.bus_schedule_eta;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.Map;

public class BusScheduleViewModel {
    private boolean success;
    private Map<String, Object> scheduleData;
    private boolean isCachedData;
    private String errorMessage;
    private boolean noBuses;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String, Object> getScheduleData() { return scheduleData; }
    public void setScheduleData(Map<String, Object> scheduleData) {
        this.scheduleData = scheduleData;
    }

    public boolean isCachedData() { return isCachedData; }
    public void setCachedData(boolean cachedData) {
        this.isCachedData = cachedData;
    }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isNoBuses() { return noBuses; }
    public void setNoBuses(boolean noBuses) {
        this.noBuses = noBuses;
    }

    // Property change support for UI updates
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
