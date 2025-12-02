package data_access;

import entities.RouteShape;
import org.jxmapviewer.viewer.GeoPosition;
import use_case.map.RouteShapeDataAccessInterface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class RouteShapeDataAccessObject implements RouteShapeDataAccessInterface {
    private Map<Integer, RouteShape> idToRouteShape = new HashMap<>();

    public RouteShapeDataAccessObject() {
        URL resource = getClass().getResource("/shapes.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {
            String line = br.readLine();
            String[] cells, s;
            int id;
            int currentRouteId = -1;
            while ((line = br.readLine()) != null) {
                cells = line.split(",");
                s = cells[0].split("-");
                if (s.length == 1) {
                    id = Integer.parseInt(cells[0]);
                }
                else {
                    id = Integer.parseInt(s[1]);
                }
                if (id == currentRouteId) {
                    RouteShape shape = idToRouteShape.get(id);
                    shape.addPoint(new GeoPosition(Double.parseDouble(cells[1]), Double.parseDouble(cells[2])));
                }
                else {
                    RouteShape routeShape = new RouteShape(id);
                    idToRouteShape.put(id, routeShape);
                    currentRouteId = id;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    @Override
    public RouteShape getShapeById(int id) {
        return idToRouteShape.get(id);
    }

    public boolean hasRoute(int id) { return idToRouteShape.containsKey(id); }
}
