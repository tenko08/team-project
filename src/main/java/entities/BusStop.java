package entities;

import java.util.List;

public class BusStop {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private List<String> routes;

    public BusStop(String id, String name, double latitude, double longitude, List<String> routes) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.routes = routes;
    }

    // Getters and setters
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public List<String> getRoutes() {
        return routes;
    }
    
    private int id;
    private int stopSequence;
    private String name;
    private Position position;

    public  BusStop(int id, int stopSequence, String name, Position position) {
        this.id = id;
        this.stopSequence = stopSequence;
        this.name = name;
        this.position = position;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "id='" + id + '\'' +
                ", name=" + name + '\n' +
                ", position=" + position + '\n'+
                ", stopSeq='" + stopSequence + '\'' +
                '}';
    }
}
