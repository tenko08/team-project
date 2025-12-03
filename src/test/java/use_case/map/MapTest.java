package use_case.map;


import data_access.RouteShapeDataAccessObject;
import entities.RouteShape;
import org.junit.Test;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import use_case.find_nearest_route.FindNearestRouteOutputBoundary;
import use_case.find_nearest_route.FindNearestRouteOutputData;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MapTest {
    private class MapPresenterMock implements MapOutputBoundary {
        private MapOutputData mapOutputData;
        private GeoPosition geoPosition;

        @Override
        public void prepareRouteView(MapOutputData mapOutputData) {
            this.mapOutputData = mapOutputData;
        }

        @Override
        public void prepareCursorWaypointView(GeoPosition geoPosition) {
            this.geoPosition = geoPosition;
        }

        @Override
        public void addWaypointChangeListener(PropertyChangeListener listener) {}

        @Override
        public void setClickPosition(GeoPosition clickPosition) {}

        public MapOutputData getMapOutputData() { return mapOutputData; }

        public GeoPosition getGeoPosition() { return geoPosition; }
    }

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

    private class FindNearestRoutePresenterMock implements FindNearestRouteOutputBoundary {
        @Override
        public void prepareSuccessView(FindNearestRouteOutputData outputData) { }

        @Override
        public void prepareFailView(String message) { }

        @Override
        public void setCursorWaypoint(GeoPosition cursorWaypoint) { }
    }

    @Test
    // test that showRoute() creates the correct list of route shapes
    public void testShowRoute() {
        MapPresenterMock mapPresenterMock = new MapPresenterMock();
        RouteShapeDataAccessInterface routeShapeDataAccessInterface = new RouteShapeDataAccessObject();
        MapInputBoundary mapInputBoundary = new MapInteractor(routeShapeDataAccessInterface, mapPresenterMock);

        String routeNumber = "1083286";
        mapInputBoundary.showRoute(routeNumber);

        URL resource = getClass().getResource("/shapes_testfile.csv");
        List<List<GeoPosition>> l = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {
            String line;
            List<GeoPosition> points = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                GeoPosition geoPosition = new GeoPosition(Double.parseDouble(fields[1]), Double.parseDouble(fields[2]));
                points.add(geoPosition);
            }
            l.add(points);
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }

        assertNotNull("Output data should not be null", mapPresenterMock.getMapOutputData());
        for (int i = 0; i < l.size(); i++) {
            assertEquals("Line 1 shape point should match the test csv file.", l.get(0).get(i),
                    mapPresenterMock.getMapOutputData().getRouteShapePoints().get(0).get(i));
        }

        routeNumber = "927";
        mapInputBoundary.showRoute(routeNumber);
        assertNotNull(mapPresenterMock.getMapOutputData().getBuses());
    }

    @Test
    public void testMarkWaypoint() {
        MapPresenterMock mapPresenterMock = new MapPresenterMock();
        RouteShapeDataAccessObjectMock routeShapeDataAccessObjectMock = new RouteShapeDataAccessObjectMock();
        MapInputBoundary mapInputBoundary = new MapInteractor(routeShapeDataAccessObjectMock, mapPresenterMock);

        JXMapViewer mapViewer = new JXMapViewer();
        mapInputBoundary.setMapViewer(mapViewer);

        FindNearestRoutePresenterMock findNearestRoutePresenterMock = new FindNearestRoutePresenterMock();
        mapInputBoundary.setFindNearestRouteOutputBoundary(findNearestRoutePresenterMock);

        Point pt = new Point(1, 1);
        MapInputData mapInputData = new MapInputData(pt);
        mapInputBoundary.markWaypoint(mapInputData);

        GeoPosition geoPos = mapViewer.convertPointToGeoPosition(pt);

        assertEquals("Geoposition of waypoint should be equal to " + geoPos, geoPos,
                mapPresenterMock.getGeoPosition());

        mapInputBoundary.markWaypoint(mapInputData);
        assertEquals("Waypoint should now be erased", null, mapPresenterMock.getGeoPosition());
    }
}
