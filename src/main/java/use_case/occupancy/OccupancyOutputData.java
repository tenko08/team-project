package use_case.occupancy;

public class OccupancyOutputData {
    private final int busId;
    private final String occupancyLevel;

    public OccupancyOutputData(int busId, String occupancyLevel) {
        this.busId = busId;
        this.occupancyLevel = occupancyLevel;
    }

    public int getBusId() {
        return busId;
    }

    public String getOccupancyLevel() {
        return occupancyLevel;
    }
}
