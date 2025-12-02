package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.search_by_route.SearchByRouteController;
import interface_adapter.search_by_route.SearchByRouteViewModel;
import entities.Bus;
import entities.Position;
import entities.Route;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.List;

public class SearchByRouteView extends JPanel {
    private final String viewName = "search_by_route";
    private final SearchByRouteController controller;
    private final SearchByRouteViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    private final JTextField routeNumberField = new JTextField(20);
    private final JTextArea resultArea = new JTextArea(25, 60);
    private final JButton searchButton = new JButton("Search Route");
    private final JButton clearButton = new JButton("Clear");
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton homeButton = new JButton("Back");
    private final PropertyChangeListener listener;

    private String lastRouteQuery = "";

    public SearchByRouteView(SearchByRouteController controller,
                             SearchByRouteViewModel viewModel,
                             ViewManagerModel viewManagerModel) {
        super(new BorderLayout());
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;

        configureComponents();
        configureLayout();
        configureListeners();

        listener = evt -> SwingUtilities.invokeLater(this::updateViewFromModel);
        viewModel.addPropertyChangeListener(listener);

        showWelcomeMessage();
    }

    private void configureComponents() {
        routeNumberField.setToolTipText("Enter a TTC route number (e.g., 36, 501)");
        routeNumberField.setMaximumSize(new Dimension(200, 30));

        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(220, 20, 60));
        clearButton.setForeground(Color.WHITE);

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

        if (viewManagerModel != null) {
            JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JButton backButton = new JButton("← Back to Map");
            backButton.addActionListener(e -> {
                viewManagerModel.setState("map");
                viewManagerModel.firePropertyChange();
            });
            homeButton.addActionListener(e -> {
                viewManagerModel.setState("landing");
                viewManagerModel.firePropertyChange();
            });
            leftBtns.add(backButton);
            leftBtns.add(homeButton);
            topBar.add(leftBtns, BorderLayout.WEST);
        }

        topBar.add(statusLabel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Search by Route Number"));

        JPanel routeRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        routeRow.add(new JLabel("Route Number:"));
        routeRow.add(routeNumberField);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.add(searchButton);
        buttonRow.add(clearButton);

        inputPanel.add(routeRow);
        inputPanel.add(buttonRow);

        add(inputPanel, BorderLayout.WEST);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Search Results"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void configureListeners() {
        searchButton.addActionListener(e -> {
            String routeNumber = routeNumberField.getText().trim();
            if (routeNumber.isEmpty()) {
                showInputError("Please enter a route number.");
                return;
            }
            lastRouteQuery = routeNumber;
            statusLabel.setText("Searching...");
            resultArea.append("Searching for buses on route " + routeNumber + "...\n");
            controller.execute(routeNumber);
        });

        clearButton.addActionListener(e -> {
            routeNumberField.setText("");
            resultArea.setText("");
            statusLabel.setText("Ready");
            lastRouteQuery = "";
        });
    }

    private void updateViewFromModel() {
        if (viewModel.isSuccess()) {
            renderResults(viewModel.getRoute(), viewModel.getBuses(), viewModel.isCachedData());
        } else {
            renderError(viewModel.getErrorMessage());
        }
    }

    private void renderResults(Route route, List<Bus> buses, boolean cached) {
        if (route != null) {
            routeNumberField.setText(String.valueOf(route.getRouteNumber()));
            lastRouteQuery = String.valueOf(route.getRouteNumber());
        }

        if (buses == null || buses.isEmpty() || route == null) {
            resultArea.setText("No buses found for the specified route.\n");
            statusLabel.setText("No buses found");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Route ").append(route.getRouteNumber()).append(" - Live Bus Locations ===\n");
        sb.append("Found ").append(buses.size()).append(" bus(es) on this route\n\n");

        for (Bus bus : buses) {
            sb.append("Bus ID: ").append(bus.getId()).append("\n");
            Position position = bus.getPosition();
            if (position != null) {
                sb.append("  Location: (")
                        .append(String.format("%.6f", position.getLatitude()))
                        .append(", ")
                        .append(String.format("%.6f", position.getLongitude()))
                        .append(")\n");
                sb.append("  Bearing: ").append(String.format("%.1f", position.getBearing())).append("°\n");
                sb.append("  Speed: ").append(String.format("%.1f", position.getSpeed())).append(" m/s\n").append("\n");;
            } else {
                sb.append("  Location: Not available\n");
            }
//            sb.append("  Occupancy: ").append(bus.getOccupancy()).append("\n\n");
        }

        if (cached) {
            sb.append("⚠ WARNING: Displaying cached data. API call failed.\n");
            sb.append("Click 'Retry' to fetch fresh data.\n");
            statusLabel.setText("Showing cached data");
        } else {
            statusLabel.setText("Live data refreshed");
        }

        resultArea.setText(sb.toString());
    }

    private void renderError(String errorMessage) {
        if (errorMessage == null || errorMessage.isBlank()) {
            errorMessage = "Unknown error occurred.";
        }
        resultArea.setText("❌ Error: " + errorMessage + "\n");
        if (errorMessage.contains("Route not found")) {
            resultArea.append("Please check the route number and try again.\n");
        }
        statusLabel.setText("Error");
    }

    private void showInputError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWelcomeMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("TTC Bus Search by Route\n");
        sb.append("=======================\n\n");
        sb.append("How to use:\n");
        sb.append("1. Enter a route number (e.g., 36, 501, 95) in the field on the left.\n");
        sb.append("2. Click 'Search Route' to fetch live buses for that route.\n");
        sb.append("3. If cached data appears, click 'Retry' to fetch live data again.\n");
        sb.append("4. Use 'Clear' to reset the view.\n\n");
        sb.append("Example route numbers: 36, 501, 502, 29, 32, 95.\n");
        resultArea.setText(sb.toString());
        statusLabel.setText("Ready");
    }

    public String getViewName() {
        return viewName;
    }
}
