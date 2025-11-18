package entities;

public class Bus {
    private int id;
    private Position position;
    private String occupancy;

    public Bus(int id, Position position, String occupancy) {
        this.id = id;
        this.position = position;
        this.occupancy = occupancy;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public String getOccupancy() {
        return occupancy;
    }

    // --- Setters ---
//    public void setId(String id) { idk if we need these
//        this.id = id;
//    }
//
//    public void setRoute(Route route) {
//        this.route = route;
//    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }

    // --- Utility ---
    public boolean isEmpty() {
        return "EMPTY".equalsIgnoreCase(occupancy);
    }

//    public boolean matchesRoute(int routeNumber) {
//        return trip != null && routeNumber  == trip.getRouteNumber();
//    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
//                ", route=" + trip +
                ", position=" + position +
                ", occupancy='" + occupancy + '\'' +
                '}';
    }
}
