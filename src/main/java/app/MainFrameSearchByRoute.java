package app;

import api.BusDataBaseAPI;
import interface_adapter.search_by_route.*;
import use_case.search_by_route.SearchByRouteInteractor;
import view.SearchByRouteView;
import entities.Bus;
import entities.Route;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrameSearchByRoute {
    private static final String TITLE = "TTC Bus Search by Route";

    // Business logic components
    private SearchByRouteView searchByRouteView;
    private SearchByRouteViewModel viewModel;

    // UI components
    private JFrame frame;
    private JTextField routeNumberField;
    private JTextArea resultArea;
    private JButton searchButton;
    private JButton retryButton;
    private JButton clearButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrameSearchByRoute().createAndShowGUI();
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
        // Initialize business logic components
        BusDataBaseAPI api = new BusDataBaseAPI();
        SearchByRouteGateway gateway = new SearchByRouteGatewayImpl(api);
        viewModel = new SearchByRouteViewModel();
        SearchByRoutePresenter presenter = new SearchByRoutePresenter(viewModel);
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);
        SearchByRouteController controller = new SearchByRouteController(interactor);
        searchByRouteView = new SearchByRouteView(controller, viewModel);

        // Listen to ViewModel changes to update UI
        viewModel.addPropertyChangeListener(evt -> updateUIFromViewModel());
    }

    private void createFrame() {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700, 600));
        frame.setLocationRelativeTo(null); // Center the window
    }

    private void setupUIComponents() {
        // Input field
        routeNumberField = new JTextField(20);
        routeNumberField.setToolTipText("Enter route number (e.g., 36, 501, 502)");

        // Buttons
        searchButton = new JButton("Search Route");
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        searchButton.setToolTipText("Search for buses on this route");

        retryButton = new JButton("Retry");
        retryButton.setBackground(new Color(255, 140, 0));
        retryButton.setForeground(Color.WHITE);
        retryButton.setToolTipText("Retry fetching fresh data");
        retryButton.setEnabled(false);

        clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(220, 20, 60));
        clearButton.setForeground(Color.WHITE);

        // Result display area
        resultArea = new JTextArea(25, 60);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createTitledBorder("Search Results"));
        resultArea.setBackground(new Color(248, 248, 255));

        // Set font
        Font resultFont = new Font("Monospaced", Font.PLAIN, 12);
        resultArea.setFont(resultFont);
    }

    private void setupLayout() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Search by Route Number"));
        inputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Route number input
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel routeLabel = new JLabel("Route Number:");
        routeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        inputPanel.add(routeLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(routeNumberField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(searchButton);
        buttonPanel.add(retryButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(buttonPanel, gbc);

        // Result panel
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Assemble main interface
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(resultScrollPane, BorderLayout.CENTER);

        frame.add(mainPanel);
    }

    private void setupEventHandlers() {
        // Search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String routeNumber = routeNumberField.getText().trim();
                if (routeNumber.isEmpty()) {
                    showError("Please enter a route number");
                    return;
                }
                resultArea.append("Searching for buses on route " + routeNumber + "...\n");
                searchByRouteView.onSearchButtonClicked(routeNumber);
            }
        });

        // Retry button
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultArea.append("Retrying to fetch fresh data...\n");
                searchByRouteView.onRetryButtonClicked();
            }
        });

        // Clear button
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
                Route route = viewModel.getRoute();
                java.util.List<Bus> buses = viewModel.getBuses();

                if (route != null && buses != null) {
                    displayBusData(route, buses);

                    if (viewModel.isCachedData()) {
                        resultArea.append("⚠ WARNING: Displaying cached data. API call failed.\n");
                        resultArea.append("Click 'Retry' to fetch fresh data.\n\n");
                        retryButton.setEnabled(true);
                    } else {
                        retryButton.setEnabled(false);
                    }
                }
            } else {
                String errorMessage = viewModel.getErrorMessage();
                if (errorMessage != null) {
                    resultArea.append("❌ Error: " + errorMessage + "\n");
                    if (errorMessage.contains("Route not found")) {
                        resultArea.append("Please check the route number and try again.\n");
                    }
                }
                retryButton.setEnabled(false);
            }
            resultArea.append("\n");
            resultArea.setCaretPosition(resultArea.getDocument().getLength());
        });
    }

    private void displayBusData(Route route, java.util.List<Bus> buses) {
        if (buses == null || buses.isEmpty() || route == null) {
            resultArea.append("No buses found for route " + route.getRouteNumber() + "\n");
            return;
        }

        resultArea.append("\n=== Route " + route.getRouteNumber() + " - Live Bus Locations ===\n");
        resultArea.append("Found " + buses.size() + " bus(es) on this route\n\n");

        for (Bus bus : buses) {
            resultArea.append("Bus ID: " + bus.getId() + "\n");

            entities.Position position = bus.getPosition();
            if (position != null) {
                resultArea.append("  Location: (" +
                        String.format("%.6f", position.getLatitude()) + ", " +
                        String.format("%.6f", position.getLongitude()) + ")\n");
                resultArea.append("  Bearing: " + String.format("%.1f", position.getBearing()) + "°\n");
                resultArea.append("  Speed: " + String.format("%.1f", position.getSpeed()) + " m/s\n");
            } else {
                resultArea.append("  Location: Not available\n");
            }

            resultArea.append("  Occupancy: " + bus.getOccupancy() + "\n");
            resultArea.append("\n");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWelcomeMessage() {
        resultArea.append("TTC Bus Search by Route\n");
        resultArea.append("====================\n\n");
        resultArea.append("How to use:\n");
        resultArea.append("1. Enter a route number in the search field\n");
        resultArea.append("2. Click 'Search Route' to find all buses on that route\n");
        resultArea.append("3. View live locations, directions, and progress of each bus\n");
        resultArea.append("4. Click 'Retry' if cached data is shown to fetch fresh data\n");
        resultArea.append("5. Click 'Clear' to clear the results\n\n");
        resultArea.append("Example route numbers: 36, 501, 502, 29, 32, 95\n\n");
    }

    private void showFrame() {
        frame.pack();
        frame.setVisible(true);

        // Show welcome message
        showWelcomeMessage();
    }
}

