package use_case.search_by_route;

import entities.Bus;
import entities.Route;
import java.util.List;

public class SearchByRouteOutputData {
    private final boolean success;
    private final Route route;
    private final List<Bus> buses;
    private final String errorMessage;
    private final boolean isCached;

    public SearchByRouteOutputData(boolean success, Route route,
                                   List<Bus> buses,
                                   String errorMessage,
                                   boolean isCached) {
        this.success = success;
        this.route = route;
        this.buses = buses;
        this.errorMessage = errorMessage;
        this.isCached = isCached;
    }

    public boolean isSuccess() {
        return success;
    }

    public Route getRoute() {
        return route;
    }

    public List<Bus> getBuses() {
        return buses;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isCached() {
        return isCached;
    }
}
