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
    private Map<Integer, ArrayList<String>> idToRoutes = new HashMap<>();
    private Map<String, RouteShape> routeToRouteShape = new HashMap<>();

    public RouteShapeDataAccessObject() {
        URL resource = getClass().getResource("/shapes.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {
            String line = br.readLine();
            String[] cells, s;
            int id;
            int currentRouteId = -1;
            String branch;
            String currentBranch = "";
            while ((line = br.readLine()) != null) {
                cells = line.split(",");
                s = cells[0].split("-");
                if (s.length == 1) {
                    id = Integer.parseInt(cells[0]);
                    branch = "0";
                }
                else {
                    id = Integer.parseInt(s[1]);
                    branch = s[2];
                }
                if (id == currentRouteId) {
                    if (branch.equals(currentBranch)) {
                        RouteShape shape = routeToRouteShape.get(id + "-" + branch);
                        shape.addPoint(new GeoPosition(Double.parseDouble(cells[1]), Double.parseDouble(cells[2])));
                    }
                    else {
                        idToRoutes.get(id).add(branch);
                        RouteShape routeShape = new RouteShape(id, branch);
                        routeShape.addPoint(new GeoPosition(Double.parseDouble(cells[1]), Double.parseDouble(cells[2])));
                        routeToRouteShape.put(id + "-" + branch, routeShape);
                        currentBranch = branch;
                    }
                }
                else {
                    RouteShape routeShape = new RouteShape(id, branch);
                    routeShape.addPoint(new GeoPosition(Double.parseDouble(cells[1]), Double.parseDouble(cells[2])));
                    routeToRouteShape.put(id + "-" + branch, routeShape);
                    ArrayList<String> branches = new ArrayList<>();
                    branches.add(branch);
                    idToRoutes.put(id, branches);
                    currentRouteId = id;
                    currentBranch = branch;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    @Override
    public RouteShape getShapeById(String routeId) {
        return routeToRouteShape.get(routeId);
    }

    public boolean hasRoute(int id) { return idToRoutes.containsKey(id); }

    public ArrayList<String> getListOfBranches(int id) { return idToRoutes.get(id); }
}
