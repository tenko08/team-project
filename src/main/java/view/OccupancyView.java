package view;

import interface_adapter.occupancy.OccupancyState;
import interface_adapter.occupancy.OccupancyViewModel;

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

    public OccupancyView(OccupancyViewModel occupancyViewModel) {
        this.occupancyViewModel = occupancyViewModel;
        this.occupancyViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        JLabel title = new JLabel("Bus Occupancy");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        occupancyList = new JList<>(listModel);
        occupancyList.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(occupancyList), BorderLayout.CENTER);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        OccupancyState state = (OccupancyState) evt.getNewValue();
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError());
        } else {
            listModel.clear();
            for (Map.Entry<Integer, String> entry : state.getBusOccupancies().entrySet()) {
                listModel.addElement("Bus ID: " + entry.getKey() + " - Occupancy: " + entry.getValue());
            }
        }
    }
    
    public String getViewName() {
        return viewName;
    }
}
