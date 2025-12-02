package use_case.find_nearest_route;

import org.jxmapviewer.viewer.GeoPosition;

public interface FindNearestRouteOutputBoundary {
    void prepareSuccessView(FindNearestRouteOutputData outputData);
    void prepareFailView(String message);
    void setCursorWaypoint(GeoPosition cursorWaypoint);
}
