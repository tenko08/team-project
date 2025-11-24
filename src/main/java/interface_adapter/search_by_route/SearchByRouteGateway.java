package interface_adapter.search_by_route;

import java.util.Map;

public interface SearchByRouteGateway {
    Map<String, Object> getBusesByRoute(String routeNumber);
}

