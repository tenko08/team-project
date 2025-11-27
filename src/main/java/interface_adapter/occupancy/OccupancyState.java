package interface_adapter.occupancy;

/**
 * The State for the Occupancy View.
 */
public class OccupancyState {
    private String occupancy = "";
    private String error = null;
    private java.util.Map<Integer, String> busOccupancies = new java.util.HashMap<>();
    private entities.Route currentRoute = null;

    public OccupancyState(OccupancyState copy) {
        this.occupancy = copy.occupancy;
        this.error = copy.error;
        this.busOccupancies = new java.util.HashMap<>(copy.busOccupancies);
        this.currentRoute = copy.currentRoute;
    }

    public OccupancyState() {
    }

    public String getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public java.util.Map<Integer, String> getBusOccupancies() {
        return busOccupancies;
    }

    public void setBusOccupancies(java.util.Map<Integer, String> busOccupancies) {
        this.busOccupancies = busOccupancies;
    }

    public entities.Route getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(entities.Route currentRoute) {
        this.currentRoute = currentRoute;
    }
}
