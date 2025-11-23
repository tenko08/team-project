package interface_adapter.find_nearest_route;

import entities.Position;
import use_case.find_nearest_route.FindNearestRouteInputBoundary;
import use_case.find_nearest_route.FindNearestRouteInputData;

public class FindNearestRouteController {

    private final FindNearestRouteInputBoundary interactor;

    public FindNearestRouteController(FindNearestRouteInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(Position position) {
        FindNearestRouteInputData inputData =
                new FindNearestRouteInputData(position);
        interactor.execute(inputData);
    }
}
