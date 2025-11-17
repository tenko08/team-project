package view;
import interface_adapter.ViewModel;
import interface_adapter.bus_schedule_eta.BusScheduleController;
import interface_adapter.bus_schedule_eta.BusScheduleViewModel;

import java.util.Map;
public class BusScheduleView {
    private final BusScheduleController controller;
    private final BusScheduleViewModel viewModel;

    public BusScheduleView(BusScheduleController controller, BusScheduleViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;

        // 监听ViewModel变化
        viewModel.addPropertyChangeListener(evt -> updateView());
    }

    public void onSearchButtonClicked(String stopId) {
        controller.execute(stopId);
    }

    private void updateView() {
        if (viewModel.isSuccess()) {
            // 显示时刻表数据
            displayScheduleData(viewModel.getScheduleData());
            if (viewModel.isCachedData()) {
                showCachedDataWarning();
            }
            if (viewModel.isNoBuses()) {
                showAlternativeRoutes();
            }
        } else {
            // 显示错误信息
            showError(viewModel.getErrorMessage());
        }
    }
    private void displayScheduleData(Map<String, Object> scheduleData) {
        // 这里实现具体的UI显示逻辑
        System.out.println("显示巴士时刻表数据: " + scheduleData);

        // 示例：解析并显示数据
        if (scheduleData != null) {
            if (scheduleData.containsKey("arrivals")) {
                System.out.println("到达时间: " + scheduleData.get("arrivals"));
            }
            if (scheduleData.containsKey("vehicles")) {
                System.out.println("车辆位置: " + scheduleData.get("vehicles"));
            }
        }
    }

    /**
     * 显示缓存数据警告
     */
    private void showCachedDataWarning() {
        System.out.println("Its a cache data");
        // 在实际UI中，可以显示一个警告图标或提示消息
    }

    /**
     * 显示替代路线
     */
    private void showAlternativeRoutes() {
        System.out.println("No arriving bus at this time, here are the substitution route：");
        // 这里可以实现获取和显示替代路线的逻辑
        // 例如：调用其他用例获取替代路线
    }

    /**
     * 显示错误信息
     */
    private void showError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
        // 在实际UI中，可以显示红色错误消息框
    }

    /**
     * 额外的UI方法 - 用于显示ETA信息
     */
    public void displayETAInfo(String stopId, String routeId) {
        controller.executeGetETA(stopId, routeId);
    }

    /**
     * 清理资源的方法
     */
    public void dispose() {
        // 移除监听器，防止内存泄漏
        // 注意：需要为ViewModel添加removePropertyChangeListener方法
    }
}
