package use_case.map;

import org.jxmapviewer.viewer.GeoPosition;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;

public interface MapOutputBoundary {
    void prepareRouteView(MapOutputData mapOutputData);
    void prepareCursorWaypointView(GeoPosition geoPosition);
    void addWaypointChangeListener(PropertyChangeListener listener);
    void setClickPosition(GeoPosition clickPosition);
}
