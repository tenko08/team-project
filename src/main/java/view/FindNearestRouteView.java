package view;

import interface_adapter.find_nearest_route.FindNearestRouteController;
import interface_adapter.find_nearest_route.FindNearestRouteState;
import interface_adapter.find_nearest_route.FindNearestRouteViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FindNearestRouteView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "FindNearestRouteView";
    private final FindNearestRouteViewModel viewModel;

    private final JTextField longInputField = new JTextField(15);
    private final JTextField latInputField = new JTextField(15);
    private final JLabel errorField = new JLabel("");

    private FindNearestRouteController controller = null;

    public FindNearestRouteView(FindNearestRouteViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel("Find Nearest Route");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(searchBtn)) {
                    final FindNearestRouteState curState = viewModel.getState();
                    controller.execute(curState.getPosition());
                }
            }
        });

        addDocumentListener(longInputField, value -> {
            validateInputs(searchBtn);
            if (errorField.getText().isEmpty() && !value.isBlank()) {
                FindNearestRouteState s = viewModel.getState();
                s.setLongitude(Double.parseDouble(value));
                viewModel.setState(s);
            }
        });

        addDocumentListener(latInputField, value -> {
            validateInputs(searchBtn);
            if (errorField.getText().isEmpty() && !value.isBlank()) {
                FindNearestRouteState s = viewModel.getState();
                s.setLatitude(Double.parseDouble(value));
                viewModel.setState(s);
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title);
        this.add(longInputField);
        this.add(latInputField);
        this.add(errorField);
        this.add(searchBtn);
    }

    public String getViewName() { return viewName; }

    public void actionPerformed(ActionEvent e) {
        System.out.println("Click " + e.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final FindNearestRouteState state = (FindNearestRouteState) evt.getNewValue();
        setFields(state);
        errorField.setText(state.getSearchError());
    }

    private void setFields(FindNearestRouteState state) {
        longInputField.setText(String.valueOf(state.getPosition().getLongitude()));
        latInputField.setText(String.valueOf(state.getPosition().getLatitude()));
    }

    public void setController(FindNearestRouteController controller) {
        this.controller = controller;
    }

    private void validateInputs(JButton searchBtn) {
        String lon = longInputField.getText().trim();
        String lat = latInputField.getText().trim();

        try {
            if (!lon.isEmpty()) Double.parseDouble(lon);
            if (!lat.isEmpty()) Double.parseDouble(lat);

            errorField.setText("");
            searchBtn.setEnabled(true);
        } catch (NumberFormatException ex) {
            errorField.setText("Invalid number format");
            searchBtn.setEnabled(false);
        }
    }


    private static void addDocumentListener(JTextField field,
                                           java.util.function.Consumer<String> callback) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { callback.accept(field.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { callback.accept(field.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { callback.accept(field.getText()); }
        });
    }
}
