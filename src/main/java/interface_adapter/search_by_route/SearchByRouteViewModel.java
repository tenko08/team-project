package interface_adapter.search_by_route;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SearchByRouteViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
