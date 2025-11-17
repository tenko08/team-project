package use_case.bus_schedule_eta;
import java.util.Map;
public interface BusScheduleOutputBoundary {
    void prepareSuccessView(BusScheduleOutputData outputData);
    void prepareFailView(String errorMessage);
    void prepareCachedView(BusScheduleOutputData outputData);
}
