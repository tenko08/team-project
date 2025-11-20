package interface_adapter.bus_schedule_eta;
import java.util.Map;
public interface BusScheduleGateway {
    Map<String, Object> getBusSchedule(String stopId);
    Map<String, Object> getBusETA(String stopId, String routeId);
}
