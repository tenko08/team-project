package use_case.occupancy;

public class OccupancyOutputData {
    private final String occupancyLevel;

    public OccupancyOutputData(String occupancyLevel) {
        this.occupancyLevel = occupancyLevel;
    }

    public String getOccupancyLevel() {
        return occupancyLevel;
    }
}
