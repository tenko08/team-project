package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Route {
    private final int routeNumber;
    private List<Trip> tripList;
    private List<BusStop> busStopList;

    public Route(int routeNumber) {
        this.tripList = new ArrayList<Trip>();
        this.busStopList = new ArrayList<BusStop>();
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

    // --- Utility ---
    public BusStop nearestBusStop(Position position) {
        double min = Double.MAX_VALUE;
        BusStop nearest = null;
        for (BusStop bs : this.busStopList) {
            double dist = position.distanceTo(bs.getPosition());
            if (dist < min) {
                min = dist;
                nearest = bs;
            }
        }
        return nearest;
    }
    @Override
    public String toString() {
        return "Route{" +
                "routeNum='" + routeNumber + '\'' +
                ", tripListSize=" + tripList.size() +
                ", busListSize='" + busStopList.size() + '\'' +
                '}';
    }


}
