package use_case.map;

import org.jxmapviewer.JXMapViewer;
import use_case.find_nearest_route.FindNearestRouteOutputBoundary;
import use_case.search_by_route.SearchByRouteOutputData;

public interface MapInputBoundary {
    void showRoute(String routeNumber);
    void markWaypoint(MapInputData mapInputData);
    void setMapViewer(JXMapViewer mapViewer);
    void setFindNearestRouteOutputBoundary(FindNearestRouteOutputBoundary findNearestRoutePresenter);
}
