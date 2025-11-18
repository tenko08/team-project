package use_case.find_nearest_route;

public interface FindNearestRouteOutputBoundary {
    void prepareSuccessView(FindNearestRouteOutputData outputData);
    void prepareFailView(String message);
}
