package use_case.map;

import org.jxmapviewer.JXMapViewer;
import use_case.find_nearest_route.FindNearestRouteInputBoundary;
import use_case.find_nearest_route.FindNearestRouteOutputBoundary;

public interface MapInputBoundary {
    void showRoute(String routeNumber);
    void markWaypoint(MapInputData mapInputData);
    void setMapViewer(JXMapViewer mapViewer);
    void setFindNearestRouteBoundaries(FindNearestRouteOutputBoundary findNearestRoutePresenter, FindNearestRouteInputBoundary findNearestRouteInputBoundary);
}
