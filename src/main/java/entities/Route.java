package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Route {
    private int routeNumber;
    private List<Bus> busList;
    private List<BusStop> busStopList;

    public Route() {
        this.busList = new ArrayList<>();
        this.busStopList = new ArrayList<>();
        this.routeNumber = routeNumber;
    }

    public Route(int routeNumber) {
        this.busList = new ArrayList<Bus>();
        this.busStopList = new ArrayList<BusStop>();
        this.routeNumber = routeNumber;
    }

    // --- Getters ---
    public int getRouteNumber() {
        return routeNumber;
    }
    public List<Bus> getBusList() {
        return busList;
    }
    public List<BusStop> getBusStopList() {
        return busStopList;
    }

    // --- Setters ---
    public void addBus(Bus bus) {
        this.busList.add(bus);
    }

    public void addAllBuses(List<Bus> buses) {this.busList = buses;}

    public void addBusStop(BusStop busStop) {
        this.busStopList.add(busStop);
    }

    public void setRouteNumber(int routeNumber) {this.routeNumber = routeNumber;};

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
                ", busListSize=" + busList.size() +
                ", busListSize='" + busStopList.size() + '\'' +
                '}';
    }


}
