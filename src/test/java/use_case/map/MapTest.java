package use_case.map;


import entities.RouteShape;
import interface_adapter.map.MapPresenter;
import interface_adapter.map.MapViewModel;
import org.junit.Test;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import use_case.find_nearest_route.FindNearestRouteOutputBoundary;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MapTest {

    private class RouteShapeDataAccessObjectMock implements RouteShapeDataAccessInterface {
        @Override
        public RouteShape getShapeById(String routeId) {
            return null;
        }

        @Override
        public boolean hasRoute(int id) {
            return false;
        }

        @Override
        public ArrayList<String> getListOfBranches(int routeNumber) {
            return null;
        }
    }

    public void testShowRoute() {
        RouteShapeDataAccessObjectMock routeShapeDataAccessObjectMock = new RouteShapeDataAccessObjectMock();
        MapViewModel mapViewModel = new MapViewModel();
        MapOutputBoundary mapPresenter = new MapPresenter(mapViewModel);
        MapInteractor mapInteractor = new MapInteractor(routeShapeDataAccessObjectMock, mapPresenter);

    }
}
