package app;

import api.BusDataBaseAPI;
import interface_adapter.bus_schedule_eta.*;
import use_case.bus_schedule_eta.BusScheduleInteractor;
import view.BusScheduleView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class MainFrameBusScheduleEta{
    private static final String TITLE = "TTC Bus Schedule & ETA Viewer";

    // 业务逻辑组件
    private BusScheduleView busScheduleView;
    private BusScheduleViewModel viewModel;

    // UI组件
    private JFrame frame;
    private JTextField stopIdField;
    private JTextField routeIdField;
    private JTextArea resultArea;
    private JButton searchScheduleButton;
    private JButton searchETAButton;
    private JButton clearButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrameBusScheduleEta().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        initializeBusinessLogic();
        createFrame();
        setupUIComponents();
        setupLayout();
        setupEventHandlers();
        showFrame();
    }

    private void initializeBusinessLogic() {
        // 初始化业务逻辑组件
        BusDataBaseAPI api = new BusDataBaseAPI();
        BusScheduleGateway gateway = new BusScheduleGatewayImpl(api);
        viewModel = new BusScheduleViewModel();
        BusSchedulePresenter presenter = new BusSchedulePresenter(viewModel);
        BusScheduleInteractor interactor = new BusScheduleInteractor(gateway, presenter);
        BusScheduleController controller = new BusScheduleController(interactor);
        busScheduleView = new BusScheduleView(controller, viewModel);

        // 监听ViewModel变化来更新UI
        viewModel.addPropertyChangeListener(evt -> updateUIFromViewModel());
    }

    private void createFrame() {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700, 600));
        frame.setLocationRelativeTo(null); // 居中显示
    }

    private void setupUIComponents() {
        // 输入字段
        stopIdField = new JTextField(20);
        stopIdField.setToolTipText("Input bus stop ID");

        routeIdField = new JTextField(20);
        routeIdField.setToolTipText("Input bus route ID");

        // 按钮
        searchScheduleButton = new JButton("Searching timetable");
        searchScheduleButton.setBackground(new Color(14, 15, 16));
        searchScheduleButton.setForeground(Color.BLACK);
        searchScheduleButton.setToolTipText("Check timetable on given bus stop");

        searchETAButton = new JButton("Searching ETA");
        searchETAButton.setBackground(new Color(16, 18, 16));
        searchETAButton.setForeground(Color.BLACK);
        searchETAButton.setToolTipText("Check the estimated arrival time of the bus on the specified route ");

        clearButton = new JButton("Clear result");
        clearButton.setBackground(new Color(14, 13, 13));
        clearButton.setForeground(Color.BLACK);

        // 结果显示区域
        resultArea = new JTextArea(25, 60);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createTitledBorder("Searching result"));
        resultArea.setBackground(new Color(248, 248, 255));

        // 设置字体
        Font resultFont = new Font("Microsoft YaHei", Font.PLAIN, 13);
        resultArea.setFont(resultFont);
    }

    private void setupLayout() {
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));

        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Check condition"));
        inputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第一行：站点ID
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel stopLabel = new JLabel("StopID:");
        stopLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        inputPanel.add(stopLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(stopIdField, gbc);

        // 第二行：路线ID
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel routeLabel = new JLabel("RouteID:");
        routeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        inputPanel.add(routeLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(routeIdField, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(searchScheduleButton);
        buttonPanel.add(searchETAButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(buttonPanel, gbc);

        // 结果面板
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // 组装主界面
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(resultScrollPane, BorderLayout.CENTER);

        frame.add(mainPanel);
    }

    private void setupEventHandlers() {
        // 查询时刻表按钮
        searchScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stopId = stopIdField.getText().trim();
                if (stopId.isEmpty()) {
                    showError("PLease enter stop ID");
                    return;
                }
                resultArea.append("Searching timetable on " + stopId + "\n");
                busScheduleView.onSearchButtonClicked(stopId);
            }
        });

        // 查询ETA按钮
        searchETAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stopId = stopIdField.getText().trim();
                String routeId = routeIdField.getText().trim();

                if (stopId.isEmpty() || routeId.isEmpty()) {
                    showError("Please input stop ID and route ID");
                    return;
                }
                resultArea.append("Searching ETA on route " + routeId + " at stop " + stopId + "\n");
                busScheduleView.displayETAInfo(stopId, routeId);
            }
        });

        // 清空结果按钮
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("");
                showWelcomeMessage();
            }
        });
    }

    private void updateUIFromViewModel() {
        SwingUtilities.invokeLater(() -> {
            if (viewModel.isSuccess()) {
                Map<String, Object> scheduleData = viewModel.getScheduleData();
                if (scheduleData != null) {
                    displayScheduleData(scheduleData);
//                    if (viewModel.isCachedData()) {
//                        resultArea.append("⚠️ 注意：显示的是缓存数据\n");
//                    }
                    if (viewModel.isNoBuses()) {
                        resultArea.append("NO bus at certain time, please choose other routes\n");
                    }
                }
            } else {
                String errorMessage = viewModel.getErrorMessage();
                if (errorMessage != null) {
                    resultArea.append("Error: " + errorMessage + "\n");
                }
            }
            resultArea.append("\n");
            resultArea.setCaretPosition(resultArea.getDocument().getLength());
        });
    }

    private void displayScheduleData(Map<String, Object> scheduleData) {
        // 显示解析后的时刻表数据
        resultArea.append("Stop: " + scheduleData.get("stopName") + " (ID: " + scheduleData.get("stopId") + ")\n");
        resultArea.append("Update Time: " + scheduleData.get("lastUpdated") + "\n");
        resultArea.append("Arriving bus: " + scheduleData.get("numberOfBuses") + "\n\n");

        List<Map<String, Object>> arrivals = (List<Map<String, Object>>) scheduleData.get("arrivals");
        if (arrivals != null && !arrivals.isEmpty()) {
            resultArea.append("route\tETA\tarriving time\tdestination\n");
            resultArea.append("----\t---\t--------\t------\n");

            for (Map<String, Object> arrival : arrivals) {
                String routeId = (String) arrival.get("routeId");
                int eta = (Integer) arrival.get("eta");
                String arrivalTime = (String) arrival.get("arrivalTime");
                String destination = (String) arrival.get("destination");

                resultArea.append(routeId + "\t" + eta + "minutes\t" + arrivalTime + "\t" + destination + "\n");
            }
        } else {
            resultArea.append("No arriving bus at that time\n");
        }

        resultArea.append("\n");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWelcomeMessage() {
        resultArea.append("TTC Bus Searching System\n");
        resultArea.append("====================\n\n");
        resultArea.append("How to use:\n");
        resultArea.append("1. Input stopID to check timetable\n");
        resultArea.append("2. Input stopID and routeID to check ETA\n");
        resultArea.append("3. Click to clear the result\n\n");
        resultArea.append("Example:\n");
        resultArea.append("StopID: 12345\n");
        resultArea.append("RouteID: 501\n\n");
    }

    private void showFrame() {
        frame.pack();
        frame.setVisible(true);

        // 显示欢迎信息
        showWelcomeMessage();
    }

}