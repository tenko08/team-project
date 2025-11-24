package use_case.find_nearest_route;

import entities.Route;
import java.util.List;

public interface FindNearestRouteDataAccessInterface {
    List<Route> getAllRoutes();
}
