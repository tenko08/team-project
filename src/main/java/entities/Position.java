package entities;

public class Position {
    private static final double EARTH_RADIUS_METERS = 6_371_000;

    private double latitude;
    private double longitude;
    private float bearing;
    private float speed;

    public Position(double latitude, double longitude, float bearing, float speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing;
        this.speed = speed;
    }

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // --- Getters ---
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getBearing() {
        return bearing;
    }

    public float getSpeed() {
        return speed;
    }

    // --- Setters ---
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    // --- Utility ---
    public double distanceTo(Position other) {
        double dx = other.latitude - this.latitude;
        double dy = other.longitude - this.longitude;
        return Math.sqrt(dx * dx + dy * dy);
    }


    @Override
    public String toString() {
        return "Position{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", bearing=" + bearing +
                ", speed=" + speed +
                '}';
    }
}
