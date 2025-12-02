package use_case.map;

import entities.RouteShape;

import java.util.ArrayList;

public interface RouteShapeDataAccessInterface {
    RouteShape getShapeById(String routeId);

    boolean hasRoute(int id);

    ArrayList<String> getListOfBranches(int routeNumber);
}
