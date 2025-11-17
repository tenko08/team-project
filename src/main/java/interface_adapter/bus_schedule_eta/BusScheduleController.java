package interface_adapter.bus_schedule_eta;
import use_case.bus_schedule_eta.BusScheduleInputBoundary;
import use_case.bus_schedule_eta.BusScheduleInputData;

public class BusScheduleController {
    private final BusScheduleInputBoundary busScheduleUseCaseInteractor;

    public BusScheduleController(BusScheduleInputBoundary busScheduleUseCaseInteractor) {
        this.busScheduleUseCaseInteractor = busScheduleUseCaseInteractor;
    }
    /**
     * 执行获取巴士时刻表用例
     * @param stopId 站点ID
     */
    public void execute(String stopId) {
        BusScheduleInputData inputData = new BusScheduleInputData(stopId);
        busScheduleUseCaseInteractor.execute(inputData);
    }

    /**
     * 执行获取特定路线巴士时刻表用例
     * @param stopId 站点ID
     * @param routeId 路线ID
     */
    public void execute(String stopId, String routeId) {
        BusScheduleInputData inputData = new BusScheduleInputData(stopId, routeId);
        busScheduleUseCaseInteractor.execute(inputData);
    }

    /**
     * 执行获取巴士ETA用例
     * @param stopId 站点ID
     * @param routeId 路线ID
     */
    public void executeGetETA(String stopId, String routeId) {
        // 这里可以调用专门的ETA用例，或者复用时刻表用例
        BusScheduleInputData inputData = new BusScheduleInputData(stopId, routeId);
        busScheduleUseCaseInteractor.execute(inputData);
    }
}
