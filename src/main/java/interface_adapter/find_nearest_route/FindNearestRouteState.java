package interface_adapter.find_nearest_route;

import entities.Position;

public class FindNearestRouteState {
    private Position position;

    public  Position getPosition() {
        return position;
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
}
