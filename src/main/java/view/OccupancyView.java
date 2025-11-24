package view;

import interface_adapter.occupancy.OccupancyController;
import interface_adapter.occupancy.OccupancyState;
import interface_adapter.occupancy.OccupancyViewModel;
import entities.Route;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class OccupancyView extends JPanel implements PropertyChangeListener {
    public final String viewName = "occupancy";
    private final OccupancyViewModel occupancyViewModel;
    private final DefaultListModel<String> listModel;
    private final JList<String> occupancyList;
    private final JTextField routeInputField;
    private final JLabel routeLabel;
    private final JButton loadButton;
    private OccupancyController controller;

    public OccupancyView(OccupancyViewModel occupancyViewModel) {
        this.occupancyViewModel = occupancyViewModel;
        this.occupancyViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        
        // Title
        JLabel title = new JLabel("Bus Occupancy");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // Top panel with route input
        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel routeInputLabel = new JLabel("Route Number:");
        routeInputField = new JTextField(10);
        loadButton = new JButton("Load Occupancy");
        topPanel.add(routeInputLabel);
        topPanel.add(routeInputField);
        topPanel.add(loadButton);
        
        routeLabel = new JLabel("No route selected");
        routeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        routeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(topPanel, BorderLayout.CENTER);
        inputPanel.add(routeLabel, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.NORTH);

        // Occupancy list
        listModel = new DefaultListModel<>();
        occupancyList = new JList<>(listModel);
        occupancyList.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(occupancyList), BorderLayout.CENTER);
        
        // Add action listener for load button
        loadButton.addActionListener(e -> {
            if (controller != null) {
                String routeNumber = routeInputField.getText().trim();
                if (routeNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a route number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Create a route and execute occupancy check
                try {
                    int routeNum = Integer.parseInt(routeNumber);
                    Route route = new Route(routeNum);
                    controller.execute(route);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid route number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
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
