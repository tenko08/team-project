package use_case.find_nearest_route;

public interface FindNearestRouteOutputBoundary {
    void prepareFailView(String message);
    void prepareSuccessView(FindNearestRouteOutputData outputData);
}
