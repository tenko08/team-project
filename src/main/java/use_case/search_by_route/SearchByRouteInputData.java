package use_case.search_by_route;

public class SearchByRouteInputData {
    private final String routeNumber;

    public SearchByRouteInputData(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getRouteNumber() {
        return routeNumber;
    }
}
