package use_case.map;

import api.BusDataBase;
import api.BusDataBaseAPI;
import entities.Bus;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import interface_adapter.PosToGeoPos;
import use_case.find_nearest_route.FindNearestRouteOutputBoundary;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapInteractor implements MapInputBoundary {
    private final RouteShapeDataAccessInterface routeShapeDataAccessObject;
    private final MapOutputBoundary mapPresenter;
    private FindNearestRouteOutputBoundary findNearestRouteOutputBoundary;
    private JXMapViewer mapViewer;
    private boolean cursorWaypointExists = false;
    private BusDataBase busDatabase = new BusDataBaseAPI();
    private int routeOfFocus = -1;

    public MapInteractor(RouteShapeDataAccessInterface routeShapeDataAccessInterface,
                         MapOutputBoundary mapOutputBoundary) {
        this.routeShapeDataAccessObject = routeShapeDataAccessInterface;
        this.mapPresenter = mapOutputBoundary;

        Runnable updateBuses = new Runnable() {
            public void run() {
                if (routeOfFocus != -1) {
                    showRoute(routeOfFocus);
                }
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(updateBuses, 0, 10, TimeUnit.SECONDS);

    }

    @Override
    public void showRoute(String routeNumber) { showRoute(Integer.parseInt(routeNumber)); }
    public void showRoute(int routeNo) {
        List<Bus> buses = busDatabase.getBusesByRouteId(routeNo);
        ArrayList<GeoPosition> busGeoPositions = new ArrayList<>();
        List<String> branches = routeShapeDataAccessObject.getListOfBranches(routeNo);
        List<List<GeoPosition>> routeShapePoints = new ArrayList();
        for (int i = 0; i < branches.size(); i++) {
            routeShapePoints.add(routeShapeDataAccessObject.getShapeById(
                    String.valueOf(routeNo) + "-" + branches.get(i)).getPoints());
        }
        if (!buses.isEmpty()) {
            for (Bus bus: buses) {
                busGeoPositions.add(PosToGeoPos.toGeoPosition(bus.getPosition()));
            }
        }
        MapOutputData mapOutputData = new MapOutputData(busGeoPositions, routeShapePoints);
        mapPresenter.prepareRouteView(mapOutputData);
        routeOfFocus = routeNo;
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
