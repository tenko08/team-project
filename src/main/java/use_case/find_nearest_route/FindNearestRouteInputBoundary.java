package use_case.find_nearest_route;

import use_case.map.MapInputBoundary;

public interface FindNearestRouteInputBoundary {
    void execute(FindNearestRouteInputData inputData);

    void setMapInteractor(MapInputBoundary mapInteractor);
}
