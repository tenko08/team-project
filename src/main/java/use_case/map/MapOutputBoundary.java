package use_case.map;

import java.beans.PropertyChangeListener;

public interface MapOutputBoundary {
    void prepareRouteView(MapOutputData mapOutputData);
    void addWaypointChangeListener(PropertyChangeListener listener);
}
