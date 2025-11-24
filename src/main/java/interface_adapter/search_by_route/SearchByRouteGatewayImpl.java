package interface_adapter.search_by_route;

import api.BusDataBaseAPI;
import java.util.Map;

public class SearchByRouteGatewayImpl implements SearchByRouteGateway {
    private final BusDataBaseAPI busDataBaseAPI;

    public SearchByRouteGatewayImpl(BusDataBaseAPI busDataBaseAPI) {
        this.busDataBaseAPI = busDataBaseAPI;
    }

    @Override
    public Map<String, Object> getBusesByRoute(String routeNumber) {
        return busDataBaseAPI.getBusesByRoute(routeNumber);
    }
}

