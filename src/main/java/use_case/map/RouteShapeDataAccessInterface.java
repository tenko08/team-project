package use_case.map;

import entities.RouteShape;

import java.util.List;

public interface RouteShapeDataAccessInterface {
    RouteShape getShapeById(int id);

    boolean hasRoute(int id);
}
