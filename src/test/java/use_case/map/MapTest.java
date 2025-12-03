package use_case.map;


import data_access.RouteShapeDataAccessObject;
import entities.RouteShape;
import interface_adapter.map.MapPresenter;
import org.junit.Test;
import org.jxmapviewer.viewer.GeoPosition;

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

        @Override
        public void prepareRouteView(MapOutputData mapOutputData) {
            this.mapOutputData = mapOutputData;
        }

        @Override
        public void prepareCursorWaypointView(GeoPosition geoPosition) {}

        @Override
        public void addWaypointChangeListener(PropertyChangeListener listener) {}

        @Override
        public void setClickPosition(GeoPosition clickPosition) {}

        public MapOutputData getMapOutputData() { return mapOutputData; }
    }

    @Test
    // test that showRoute() creates the correct list of route shapes
    public void testRouteToRouteShape() {
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
    }
}
