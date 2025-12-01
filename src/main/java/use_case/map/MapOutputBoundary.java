package use_case.map;

import java.beans.PropertyChangeListener;

public interface MapOutputBoundary {
    void prepareBusView(BusListOutput busList);
    void addWaypointChangeListener(PropertyChangeListener listener);
}
