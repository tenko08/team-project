package interface_adapter.bus_schedule_eta;
import use_case.bus_schedule_eta.BusScheduleOutputBoundary;
import use_case.bus_schedule_eta.BusScheduleOutputData;
import interface_adapter.bus_schedule_eta.BusScheduleViewModel;

import java.util.List;
import java.util.Map;

public class BusSchedulePresenter implements BusScheduleOutputBoundary{
    private final BusScheduleViewModel viewModel;

    public BusSchedulePresenter(BusScheduleViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(BusScheduleOutputData outputData) {
        Map<String, Object> scheduleData = outputData.getScheduleData();

        // 简化显示，不显示完整数据
        if (scheduleData != null) {
            // 移除不必要的数据显示
            scheduleData.remove("completeData");
            scheduleData.remove("routes");
            scheduleData.remove("stops");
        }

        // 更新ViewModel
        viewModel.setSuccess(true);
        viewModel.setScheduleData(scheduleData);
        viewModel.setCachedData(false);
        viewModel.setErrorMessage(null);

        // 检查是否有巴士
        boolean noBuses = false;
        if (scheduleData != null && scheduleData.containsKey("arrivals")) {
            List<Map<String, Object>> arrivals = (List<Map<String, Object>>) scheduleData.get("arrivals");
            noBuses = arrivals == null || arrivals.isEmpty();
        }
        viewModel.setNoBuses(noBuses);

        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareCachedView(BusScheduleOutputData outputData) {
        // 处理缓存数据情况
        Map<String, Object> scheduleData = outputData.getScheduleData();

        // 更新ViewModel
        viewModel.setSuccess(true);
        viewModel.setScheduleData(scheduleData);
        viewModel.setCachedData(true);
        viewModel.setErrorMessage("显示缓存数据");
        viewModel.setNoBuses(outputData.isNoBuses());

        // 通知观察者数据已更新
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        // 处理失败情况
        viewModel.setSuccess(false);
        viewModel.setScheduleData(null);
        viewModel.setCachedData(false);
        viewModel.setErrorMessage(errorMessage);
        viewModel.setNoBuses(false);

        // 通知观察者数据已更新
        viewModel.firePropertyChanged();

        // 可以根据错误类型进行特殊处理
        handleSpecificFailures(errorMessage);
    }

    /**
     * 准备替代路线建议
     */
    private void prepareAlternativeRoutes(String stopId) {
        // 这里可以实现获取和显示替代路线的逻辑
        System.out.println("为站点 " + stopId + " 准备替代路线建议");
        // 可以调用其他用例来获取替代路线
    }

    /**
     * 处理特定的失败情况
     */
    private void handleSpecificFailures(String errorMessage) {
        if ("unable to estimate ETA".equals(errorMessage)) {
            // 专门处理ETA无法估算的情况
            System.out.println("无法估算巴士到达时间");
        } else if (errorMessage.contains("Unable to load schedule data")) {
            // 处理时刻表数据加载失败
            System.out.println("时刻表数据加载失败，请检查网络连接");
        }
    }
}
