package entities;

public class Trip {
    private int id;
    private Route route;
    private Bus bus;

    public  Trip(int id, Route route,  Bus bus) {
        this.id = id;
        this.route = route;
        this.bus = bus;
    }

    public int getId() {
        return id;
    }

    public int getRouteNumber() {
        return route.getRouteNumber();
    }
}
