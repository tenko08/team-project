package use_case.search_by_route;

public interface SearchByRouteOutputBoundary {
    void prepareSuccessView(SearchByRouteOutputData outputData);
    void prepareCachedView(SearchByRouteOutputData outputData);
    void prepareFailView(String errorMessage);
}
