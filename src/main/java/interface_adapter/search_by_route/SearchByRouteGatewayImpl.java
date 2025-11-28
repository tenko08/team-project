package interface_adapter.search_by_route;

import api.BusDataBaseAPI;
import data_access.BusDataAccessObject;

import java.util.Map;

public class SearchByRouteGatewayImpl implements SearchByRouteGateway {
    private final BusDataAccessObject busDataAccessObject;

    public SearchByRouteGatewayImpl(BusDataAccessObject busDataAccessObject) {
        this.busDataAccessObject = busDataAccessObject;
    }

    @Override
    public Map<String, Object> getBusesByRoute(String routeNumber) {
        return busDataAccessObject.getBusesByRoute(routeNumber);
    }
}

