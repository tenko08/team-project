package interface_adapter.bus_schedule_eta;
import api.BusDataBaseAPI;

import java.util.Map;
public class BusScheduleGatewayImpl implements BusScheduleGateway {
    private final BusDataBaseAPI busDataBaseAPI;

    public BusScheduleGatewayImpl(BusDataBaseAPI busDataBaseAPI) {
        this.busDataBaseAPI = busDataBaseAPI;
    }

    @Override
    public Map<String, Object> getBusSchedule(String stopId) {
        return busDataBaseAPI.getBusSchedule(stopId);
    }

    @Override
    public Map<String, Object> getBusETA(String stopId, String routeId) {
        return busDataBaseAPI.getBusETA(stopId, routeId);
    }
}
