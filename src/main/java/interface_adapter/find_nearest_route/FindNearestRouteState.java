package interface_adapter.find_nearest_route;

import entities.BusStop;
import entities.Position;
import entities.Route;

public class FindNearestRouteState {
    private Position position = new Position(0,0);
    private Route route;
    private BusStop busStop;
    private double distance;

    private String searchError;

    public Position getPosition() {
        return position;
    }

    public String getSearchError() {
        return searchError;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setLongitude(double longitude) {
        this.position.setLongitude(longitude);
    }

    public void setLatitude(double latitude) {
        this.position.setLatitude(latitude);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setBusStop(BusStop busStop) {
        this.busStop = busStop;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setSearchError(String searchError) {
        this.searchError = searchError;
    }
}
