package use_case.bus_schedule_eta;
import entities.BusStop;
import interface_adapter.bus_schedule_eta.BusScheduleGateway;
import use_case.search_by_route.SearchByRouteInputBoundary;
import use_case.search_by_route.SearchByRouteInputData;

import java.util.Map;

public class BusScheduleInteractor implements BusScheduleInputBoundary {
    private final BusScheduleGateway busScheduleGateway;
    private final BusScheduleOutputBoundary outputBoundary;
    private final SearchByRouteInputBoundary searchByRouteInputBoundary;

    public BusScheduleInteractor(BusScheduleGateway busScheduleGateway,
                                 BusScheduleOutputBoundary outputBoundary) {
        this.busScheduleGateway = busScheduleGateway;
        this.outputBoundary = outputBoundary;
        this.searchByRouteInputBoundary = null;
    }

    public BusScheduleInteractor(BusScheduleGateway busScheduleGateway, BusScheduleOutputBoundary outputBoundary,
                                 SearchByRouteInputBoundary searchByRouteInputBoundary) {
        this.busScheduleGateway = busScheduleGateway;
        this.outputBoundary = outputBoundary;
        this.searchByRouteInputBoundary = searchByRouteInputBoundary;
    }

    @Override
    public void execute(BusScheduleInputData inputData) {
        String stopId = inputData.getStopId();

        try {
            // 获取巴士时刻表数据
            Map<String, Object> result = busScheduleGateway.getBusSchedule(stopId);

            boolean success = (Boolean) result.getOrDefault("success", false);
            boolean isCached = (Boolean) result.getOrDefault("cached", false);
            boolean noBuses = (Boolean) result.getOrDefault("noBuses", false);

            if (success) {
                Map<String, Object> scheduleData = (Map<String, Object>) result.get("data");

                BusScheduleOutputData outputData = new BusScheduleOutputData(
                        true,
                        stopId,
                        scheduleData,
                        null,
                        isCached,
                        noBuses
                );

                if (isCached) {
                    outputBoundary.prepareCachedView(outputData);
                } else {
                    outputBoundary.prepareSuccessView(outputData);
                }

                searchByRouteInputBoundary.execute(new SearchByRouteInputData(inputData.getRouteId()));

            } else {
                String errorMessage = (String) result.getOrDefault("message", "Unknown error");
                outputBoundary.prepareFailView(errorMessage);
            }

        } catch (Exception e) {
            outputBoundary.prepareFailView("System error: " + e.getMessage());
        }
    }
}

