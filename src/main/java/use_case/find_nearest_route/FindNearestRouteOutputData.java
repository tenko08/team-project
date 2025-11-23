package use_case.find_nearest_route;

import entities.Route;
import entities.BusStop;

public class FindNearestRouteOutputData {

    private final Route route;
    private final BusStop busStop;
    private final double distance;

    public FindNearestRouteOutputData(Route route, BusStop busStop, double distance) {
        this.route = route;
        this.busStop = busStop;
        this.distance = distance;
    }

    public Route getRoute() {
        return route;
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "FindNearestRouteOutputData{" +
                "route=" + route +
                ", busStop=" + busStop +
                ", distance=" + distance +
                '}';
    }

}
