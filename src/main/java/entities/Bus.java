package entities;

public class Bus {
    private int id ;
    public String routeId;
    private double latitude;
    private double longitude;
    private int occupancy;
    private String timestamp;

    public Bus() {
        // 可以设置默认值或留空
        this.id = 0;
        this.routeId = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.occupancy = 0;
        this.timestamp = "";
    }

    // 原有的有参构造函数
    public Bus(int id, String routeId, double latitude, double longitude, int occupancy, String timestamp) {
        this.id = id;
        this.routeId = routeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.occupancy = occupancy;
        this.timestamp = timestamp;
    }

    public int getId(){
        return id;
    }
    public String getRouteId(){
        return routeId;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public int getOccupancy() {
        return occupancy;
    }
    public String getTimestamp(){
        return timestamp;
    }
}
