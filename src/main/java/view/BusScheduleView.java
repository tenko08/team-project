//package view;
//import interface_adapter.ViewModel;
//import interface_adapter.bus_schedule_eta.BusScheduleController;
//import interface_adapter.bus_schedule_eta.BusScheduleViewModel;
//
//import javax.swing.*;
//import java.util.Map;
//public class BusScheduleView extends JPanel {
//
//    private final BusScheduleController controller;
//    private final BusScheduleViewModel viewModel;
//
//    public BusScheduleView(BusScheduleController controller, BusScheduleViewModel viewModel) {
//        this.controller = controller;
//        this.viewModel = viewModel;
//
//        // 监听ViewModel变化
//        viewModel.addPropertyChangeListener(evt -> updateView());
//    }
//
//    public void onSearchButtonClicked(String stopId) {
//        controller.execute(stopId);
//    }
//
//    private void updateView() {
//        if (viewModel.isSuccess()) {
//            // 显示时刻表数据
//            displayScheduleData(viewModel.getScheduleData());
//            if (viewModel.isCachedData()) {
//                showCachedDataWarning();
//            }
//            if (viewModel.isNoBuses()) {
//                showAlternativeRoutes();
//            }
//        } else {
//            // 显示错误信息
//            showError(viewModel.getErrorMessage());
//        }
//    }
//    private void displayScheduleData(Map<String, Object> scheduleData) {
//        // 这里实现具体的UI显示逻辑
//        System.out.println("显示巴士时刻表数据: " + scheduleData);
//
//        // 示例：解析并显示数据
//        if (scheduleData != null) {
//            if (scheduleData.containsKey("arrivals")) {
//                System.out.println("到达时间: " + scheduleData.get("arrivals"));
//            }
//            if (scheduleData.containsKey("vehicles")) {
//                System.out.println("车辆位置: " + scheduleData.get("vehicles"));
//            }
//        }
//    }
//
//    /**
//     * 显示缓存数据警告
//     */
//    private void showCachedDataWarning() {
//        System.out.println("Its a cache data");
//        // 在实际UI中，可以显示一个警告图标或提示消息
//    }
//
//    /**
//     * 显示替代路线
//     */
//    private void showAlternativeRoutes() {
//        System.out.println("No arriving bus at this time, here are the substitution route：");
//        // 这里可以实现获取和显示替代路线的逻辑
//        // 例如：调用其他用例获取替代路线
//    }
//
//    /**
//     * 显示错误信息
//     */
//    private void showError(String errorMessage) {
//        System.out.println("Error: " + errorMessage);
//        // 在实际UI中，可以显示红色错误消息框
//    }
//
//    /**
//     * 额外的UI方法 - 用于显示ETA信息
//     */
//    public void displayETAInfo(String stopId, String routeId) {
//        controller.executeGetETA(stopId, routeId);
//    }
//
//    /**
//     * 清理资源的方法
//     */
//    public void dispose() {
//        // 移除监听器，防止内存泄漏
//        // 注意：需要为ViewModel添加removePropertyChangeListener方法
//    }
//
//}
package view;

import interface_adapter.bus_schedule_eta.BusScheduleController;
import interface_adapter.bus_schedule_eta.BusScheduleViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.List;

public class BusScheduleView extends JPanel {
    private final String viewName = "bus_schedule";
    private final BusScheduleController controller;
    private final BusScheduleViewModel viewModel;

    private final JTextField stopIdField = new JTextField(20);
    private final JTextArea resultArea = new JTextArea(25, 60);
    private final JButton searchButton = new JButton("Search Schedule");
    private final JButton clearButton = new JButton("Clear");
    private final JLabel statusLabel = new JLabel(" ");
    private final PropertyChangeListener listener;

    private String lastStopQuery = "";

    public BusScheduleView(BusScheduleController controller, BusScheduleViewModel viewModel) {
        super(new BorderLayout());
        this.controller = controller;
        this.viewModel = viewModel;

        configureComponents();
        configureLayout();
        configureListeners();

        listener = evt -> SwingUtilities.invokeLater(this::updateViewFromModel);
        viewModel.addPropertyChangeListener(listener);

        showWelcomeMessage();
    }

    private void configureComponents() {
        stopIdField.setToolTipText("Enter a bus stop ID (e.g., 12345)");
        stopIdField.setMaximumSize(new Dimension(200, 30));

        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.BLACK);
        clearButton.setBackground(new Color(220, 20, 60));
        clearButton.setForeground(Color.BLACK);

        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBackground(new Color(248, 248, 255));

        statusLabel.setForeground(new Color(60, 60, 60));
    }

    private void configureLayout() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topBar.add(statusLabel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Search Bus Schedule by Stop ID"));

        JPanel stopIdRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stopIdRow.add(new JLabel("Stop ID:"));
        stopIdRow.add(stopIdField);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.add(searchButton);
        buttonRow.add(clearButton);

        inputPanel.add(stopIdRow);
        inputPanel.add(buttonRow);

        add(inputPanel, BorderLayout.WEST);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Schedule Results"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void configureListeners() {
        searchButton.addActionListener(e -> {
            String stopId = stopIdField.getText().trim();
            if (stopId.isEmpty()) {
                showInputError("Please enter a stop ID.");
                return;
            }
            lastStopQuery = stopId;
            statusLabel.setText("Searching...");
            resultArea.append("Searching for bus schedule at stop " + stopId + "...\n");
            controller.execute(stopId);
        });

        clearButton.addActionListener(e -> {
            stopIdField.setText("");
            resultArea.setText("");
            statusLabel.setText("Ready");
            lastStopQuery = "";
        });
    }

    private void updateViewFromModel() {
        if (viewModel.isSuccess()) {
            renderResults(viewModel.getScheduleData(), viewModel.isCachedData(), viewModel.isNoBuses());
        } else {
            renderError(viewModel.getErrorMessage());
        }
    }

    private void renderResults(Map<String, Object> scheduleData, boolean cached, boolean noBuses) {
        if (scheduleData == null || scheduleData.isEmpty()) {
            resultArea.setText("No schedule data found for the specified stop.\n");
            statusLabel.setText("No data found");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Bus Schedule for Stop ").append(lastStopQuery).append(" ===\n\n");

        // 显示站点信息
        if (scheduleData.containsKey("stopName")) {
            sb.append("Stop Name: ").append(scheduleData.get("stopName")).append("\n");
        }
        if (scheduleData.containsKey("lastUpdated")) {
            sb.append("Last Updated: ").append(scheduleData.get("lastUpdated")).append("\n");
        }
        sb.append("\n");

        // 显示到达时间 - 美化显示
        if (scheduleData.containsKey("arrivals")) {
            Object arrivalsObj = scheduleData.get("arrivals");
            if (arrivalsObj instanceof List) {
                List<?> arrivals = (List<?>) arrivalsObj;

                if (arrivals.isEmpty()) {
                    sb.append("No buses arriving at this time.\n\n");
                } else {
                    sb.append("Upcoming Arrivals:\n");
                    sb.append("─────────────────────────────────────────────\n");

                    for (int i = 0; i < arrivals.size(); i++) {
                        Object item = arrivals.get(i);
                        if (item instanceof Map) {
                            Map<String, Object> arrival = (Map<String, Object>) item;

                            // 提取信息，提供默认值防止空指针
                            String routeName = getStringValue(arrival, "routeName", "Unknown Route");
                            String routeId = getStringValue(arrival, "routeId", "N/A");
                            String destination = getStringValue(arrival, "destination", "Unknown Destination");
                            String arrivalTime = getStringValue(arrival, "arrivalTime", "N/A");
                            Object etaObj = arrival.get("eta");

                            sb.append((i + 1)).append(". ");
                            sb.append(routeName).append(" → ").append(destination).append("\n");
                            sb.append("   Route ID: ").append(routeId);

                            if (etaObj != null) {
                                sb.append(" | ETA: ").append(etaObj).append(" minutes");
                            }

                            sb.append(" | Arrival: ").append(arrivalTime).append("\n");

                            if (i < arrivals.size() - 1) {
                                sb.append("   ───────────────────────────────────────────\n");
                            }
                        }
                    }
                    sb.append("─────────────────────────────────────────────\n\n");
                }
            }
        }

        // 显示车辆数量信息
        if (scheduleData.containsKey("numberOfBuses")) {
            sb.append("Total Buses: ").append(scheduleData.get("numberOfBuses")).append("\n\n");
        }

        // 显示其他数据（如果有）
        for (Map.Entry<String, Object> entry : scheduleData.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("arrivals") && !key.equals("stopName") &&
                    !key.equals("lastUpdated") && !key.equals("numberOfBuses") &&
                    !key.equals("stopId")) {
                sb.append(key).append(": ").append(entry.getValue()).append("\n");
            }
        }

        // 显示缓存警告
        if (cached) {
            sb.append("\n⚠ WARNING: Displaying cached data. Live data may not be available.\n");
            statusLabel.setText("Showing cached data");
        } else {
            statusLabel.setText("Live data loaded");
        }

        // 显示无巴士警告和替代路线
        if (noBuses) {
            sb.append("\n🚫 No buses arriving at this time.\n");
            showAlternativeRoutes(sb);
        }

        resultArea.setText(sb.toString());
    }

    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private void showAlternativeRoutes(StringBuilder sb) {
        sb.append("\nAlternative Suggestions:\n");
        sb.append("• Check nearby stops for alternative routes\n");
        sb.append("• Try searching during peak hours\n");
        sb.append("• Verify the stop ID is correct\n");
        sb.append("• Check for service advisories or delays\n");
    }


    private void renderError(String errorMessage) {
        if (errorMessage == null || errorMessage.isBlank()) {
            errorMessage = "Unknown error occurred.";
        }
        resultArea.setText("❌ Error: " + errorMessage + "\n");
        if (errorMessage.contains("Stop not found")) {
            resultArea.append("Please check the stop ID and try again.\n");
        }
        statusLabel.setText("Error");
    }

    private void showInputError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWelcomeMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bus Schedule Search\n");
        sb.append("===================\n\n");
        sb.append("How to use:\n");
        sb.append("1. Enter a bus stop ID in the field on the left.\n");
        sb.append("2. Click 'Search Schedule' to fetch bus arrivals.\n");
        sb.append("3. View real-time bus schedule information.\n");
        sb.append("4. Use 'Clear' to reset the view.\n\n");
        sb.append("Features:\n");
        sb.append("• Real-time bus arrival predictions\n");
        sb.append("• Vehicle location information\n");
        sb.append("• Cached data fallback\n");
        sb.append("• Alternative route suggestions\n");
        resultArea.setText(sb.toString());
        statusLabel.setText("Ready");
    }

    public String getViewName() {
        return viewName;
    }

    /**
     * 清理资源的方法
     */
    public void dispose() {
        if (viewModel != null && listener != null) {
            viewModel.removePropertyChangeListener(listener);
        }
    }
}
