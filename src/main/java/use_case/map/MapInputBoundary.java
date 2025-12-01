package use_case.map;

import use_case.search_by_route.SearchByRouteOutputData;

public interface MapInputBoundary {
    void showBuses(SearchByRouteOutputData searchByRouteOutputData);
}
