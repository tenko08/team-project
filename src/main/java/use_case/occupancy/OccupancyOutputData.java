package use_case.occupancy;

import java.util.HashMap;
import java.util.Map;

public class OccupancyOutputData {
    private final Map<Integer, String> busOccupancies;
    private final int busId;
    private final String occupancyLevel;
    private final entities.Route route;

    public OccupancyOutputData(int busId, String occupancyLevel, entities.Route route) {
        this.busId = busId;
        this.occupancyLevel = occupancyLevel;
        this.busOccupancies = new HashMap<>();
        this.busOccupancies.put(busId, occupancyLevel);
        this.route = route;
    }

    public OccupancyOutputData(Map<Integer, String> busOccupancies, entities.Route route) {
        this.busId = -1;
        this.occupancyLevel = null;
        this.busOccupancies = busOccupancies;
        this.route = route;
    }

    public int getBusId() {
        return busId;
    }

    public String getOccupancyLevel() {
        return occupancyLevel;
    }

    public Map<Integer, String> getBusOccupancies() {
        return busOccupancies;
    }

    public entities.Route getRoute() {
        return route;
    }
}
