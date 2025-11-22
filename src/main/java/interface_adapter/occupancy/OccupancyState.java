package interface_adapter.occupancy;

/**
 * The State for the Occupancy View.
 */
public class OccupancyState {
    private String occupancy = "";
    private String error = null;

    public OccupancyState(OccupancyState copy) {
        this.occupancy = copy.occupancy;
        this.error = copy.error;
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
}
