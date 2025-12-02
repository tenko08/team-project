package use_case.map;

import entities.Bus;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import interface_adapter.PosToGeoPos;
import use_case.find_nearest_route.FindNearestRouteOutputBoundary;
import use_case.search_by_route.SearchByRouteOutputData;

import java.util.ArrayList;
import java.util.List;

public class MapInteractor implements MapInputBoundary {
    private final RouteShapeDataAccessInterface routeShapeDataAccessObject;
    private final MapOutputBoundary mapPresenter;
    private FindNearestRouteOutputBoundary findNearestRouteOutputBoundary;
    private JXMapViewer mapViewer;
    private boolean cursorWaypointExists = false;

    public MapInteractor(RouteShapeDataAccessInterface routeShapeDataAccessInterface,
                         MapOutputBoundary mapOutputBoundary) {
        this.routeShapeDataAccessObject = routeShapeDataAccessInterface;
        this.mapPresenter = mapOutputBoundary;
    }

    @Override
    public void showRoute(SearchByRouteOutputData searchByRouteOutputData) {
        List<Bus> buses = searchByRouteOutputData.getBuses();
        ArrayList<GeoPosition> busGeoPositions = new ArrayList<>();
        int routeNumber = searchByRouteOutputData.getRoute().getRouteNumber();
        List<String> branches = routeShapeDataAccessObject.getListOfBranches(routeNumber);
        List<List<GeoPosition>> routeShapePoints = new ArrayList();
        for (int i = 0; i < branches.size(); i++) {
            routeShapePoints.add(routeShapeDataAccessObject.getShapeById(
                    routeNumber + "-" + branches.get(i)).getPoints());
        }
        if (!buses.isEmpty()) {
            for (Bus bus: buses) {
                busGeoPositions.add(PosToGeoPos.toGeoPosition(bus.getPosition()));
            }
        }
        MapOutputData mapOutputData = new MapOutputData(busGeoPositions, routeShapePoints);
        mapPresenter.prepareRouteView(mapOutputData);
    }

    public void markWaypoint (MapInputData mapInputData) {
        if (cursorWaypointExists) {
            cursorWaypointExists = false;
            mapPresenter.prepareCursorWaypointView(null);
            findNearestRouteOutputBoundary.setCursorWaypoint(null);
        }
        else {
            GeoPosition geoPos = mapViewer.convertPointToGeoPosition(mapInputData.getPoint());
            mapPresenter.setClickPosition(geoPos);
            mapPresenter.prepareCursorWaypointView(geoPos);
            findNearestRouteOutputBoundary.setCursorWaypoint(geoPos);
            cursorWaypointExists = true;
        }
    }

    public void setMapViewer(JXMapViewer mapViewer) { this.mapViewer = mapViewer; }

    public void setFindNearestRouteOutputBoundary(FindNearestRouteOutputBoundary findNearestRoutePresenter) {
        this.findNearestRouteOutputBoundary = findNearestRoutePresenter;
    }
}
