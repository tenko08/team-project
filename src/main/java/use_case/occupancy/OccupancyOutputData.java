package use_case.occupancy;

public class OccupancyOutputData {
    private final String occupancyLevel;
    private final int busId;

    public OccupancyOutputData(int busId, String occupancyLevel) {
        this.busId = busId;
        this.occupancyLevel = occupancyLevel;
    }

    public String getOccupancyLevel() {
        return occupancyLevel;
    }

    public int getBusId() {
        return busId;
    }
}
