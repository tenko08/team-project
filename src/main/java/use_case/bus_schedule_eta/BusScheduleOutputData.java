package use_case.bus_schedule_eta;
import java.util.List;
import java.util.Map;

public class BusScheduleOutputData {
    private final boolean success;
    private final String stopId;
    private final Map<String, Object> scheduleData;
    private final String errorMessage;
    private final boolean isCached;
    private final boolean noBuses;

    public BusScheduleOutputData(boolean success, String stopId,
                                 Map<String, Object> scheduleData,
                                 String errorMessage,
                                 boolean isCached,
                                 boolean noBuses) {
        this.success = success;
        this.stopId = stopId;
        this.scheduleData = scheduleData;
        this.errorMessage = errorMessage;
        this.isCached = isCached;
        this.noBuses = noBuses;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getStopId() { return stopId; }
    public Map<String, Object> getScheduleData() { return scheduleData; }
    public String getErrorMessage() { return errorMessage; }
    public boolean isCached() { return isCached; }
    public boolean isNoBuses() { return noBuses; }

    // 便捷方法
    public boolean shouldShowAlternativeRoutes() {
        return noBuses && success;
    }
}
