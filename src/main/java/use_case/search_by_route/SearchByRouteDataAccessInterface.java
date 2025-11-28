package use_case.search_by_route;

import java.util.Map;

public interface SearchByRouteDataAccessInterface {
    Map<String, Object> getBusesByRoute(String routeNumber);
}
