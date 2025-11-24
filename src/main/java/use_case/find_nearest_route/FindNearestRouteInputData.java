package use_case.find_nearest_route;

import entities.Position;

public class FindNearestRouteInputData {
    private Position position;

    public FindNearestRouteInputData(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
