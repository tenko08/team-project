package interface_adapter.search_by_route;

import use_case.search_by_route.SearchByRouteInputBoundary;
import use_case.search_by_route.SearchByRouteInputData;

public class SearchByRouteController {
    private final SearchByRouteInputBoundary searchByRouteUseCaseInteractor;

    public SearchByRouteController(SearchByRouteInputBoundary searchByRouteUseCaseInteractor) {
        this.searchByRouteUseCaseInteractor = searchByRouteUseCaseInteractor;
    }

    public void execute(String routeNumber) {
        SearchByRouteInputData inputData = new SearchByRouteInputData(routeNumber);
        searchByRouteUseCaseInteractor.execute(inputData);
    }

}
