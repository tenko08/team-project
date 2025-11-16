package entities;

import java.util.List;

public class Route {
    private final int routeNumber;
    private List<Trip> tripList;
    private List<BusStop> busStopList;

    public Route(int routeNumber) {
        this.routeNumber = routeNumber;
    }

    // --- Getters ---
    public int getRouteNumber() {
        return routeNumber;
    }

    public List<Trip> getTripList() {
        return tripList;
    }
    public List<BusStop> getBusStopList() {
        return busStopList;
    }

    // --- Setters ---
    public void addTrip(Trip trip) {
        this.tripList.add(trip);
    }

    public void addBusStop(BusStop busStop) {
        this.busStopList.add(busStop);
    }



}
