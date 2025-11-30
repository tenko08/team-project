package view;

import interface_adapter.occupancy.OccupancyController;
import interface_adapter.occupancy.OccupancyState;
import interface_adapter.occupancy.OccupancyViewModel;
import interface_adapter.ViewManagerModel;
import entities.Route;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class OccupancyView extends JPanel implements PropertyChangeListener {
    public final String viewName = "occupancy";
    private final OccupancyViewModel occupancyViewModel;
    private final ViewManagerModel viewManagerModel;
    private final DefaultListModel<String> listModel;
    private final JList<String> occupancyList;
    private final JTextField routeInputField;
    private final JLabel routeLabel;
    private final JButton loadButton;
    private final JButton backButton;
    private final JButton homeButton = new JButton("Back");
    private OccupancyController controller;

    public OccupancyView(OccupancyViewModel occupancyViewModel, ViewManagerModel viewManagerModel) {
        this.occupancyViewModel = occupancyViewModel;
        this.viewManagerModel = viewManagerModel;
        this.occupancyViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        // Top bar with back button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("← Back to Map");
        backButton.addActionListener(e -> {
            viewManagerModel.setState("map");
            viewManagerModel.firePropertyChange();
        });
        homeButton.addActionListener(e -> {
            viewManagerModel.setState("landing");
            viewManagerModel.firePropertyChange();
        });
        topBar.add(backButton);
        topBar.add(homeButton);
        add(topBar, BorderLayout.NORTH);

        // Main panel to hold content
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header panel for Title and Input
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("Bus Occupancy");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(10));

        // Top panel with route input
        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel routeInputLabel = new JLabel("Route Number:");
        routeInputField = new JTextField(10);
        loadButton = new JButton("Load Occupancy");
        topPanel.add(routeInputLabel);
        topPanel.add(routeInputField);
        topPanel.add(loadButton);

        routeLabel = new JLabel("No route selected");
        routeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        routeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        routeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        headerPanel.add(topPanel);
        headerPanel.add(routeLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Occupancy list
        listModel = new DefaultListModel<>();
        occupancyList = new JList<>(listModel);
        occupancyList.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(new JScrollPane(occupancyList), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Add action listener for load button
        loadButton.addActionListener(e -> {
            if (controller != null) {
                String routeNumber = routeInputField.getText().trim();
                if (routeNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a route number", "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Create a route and execute occupancy check
                try {
                    int routeNum = Integer.parseInt(routeNumber);
                    Route route = new Route(routeNum);
                    controller.execute(route);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid route number", "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void setController(OccupancyController controller) {
        this.controller = controller;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        OccupancyState state = (OccupancyState) evt.getNewValue();
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // Update route label
            if (state.getCurrentRoute() != null) {
                routeLabel.setText("Route " + state.getCurrentRoute().getRouteNumber() + " - " +
                        state.getBusOccupancies().size() + " bus(es)");
            } else {
                routeLabel.setText("No route selected");
            }

            // Update occupancy list
            listModel.clear();
            if (state.getBusOccupancies().isEmpty()) {
                listModel.addElement("No occupancy data available. Load a route to see bus occupancy.");
            } else {
                for (Map.Entry<Integer, String> entry : state.getBusOccupancies().entrySet()) {
                    listModel.addElement("Bus ID: " + entry.getKey() + " - Occupancy: " + entry.getValue());
                }
            }
        }
    }

    public String getViewName() {
        return viewName;
    }
}
