package view;

import interface_adapter.find_nearest_route.FindNearestRouteController;
import interface_adapter.find_nearest_route.FindNearestRouteState;
import interface_adapter.find_nearest_route.FindNearestRouteViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.InternationalFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FindNearestRouteView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "FindNearestRouteView";
    private final FindNearestRouteViewModel viewModel;

    private final JFormattedTextField longInputField = createDecimalField();
    private final JFormattedTextField latInputField = createDecimalField();

    private FindNearestRouteController controller = null;

    public FindNearestRouteView(FindNearestRouteViewModel viewModel) {
        this.viewModel = viewModel;
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
            FindNearestRouteState s = viewModel.getState();
            if (!value.isBlank()) {
                s.setLongitude(Double.parseDouble(value));
                viewModel.setState(s);
            }
        });

        addDocumentListener(latInputField, value -> {
            FindNearestRouteState s = viewModel.getState();
            if (!value.isBlank()) {
                s.setLatitude(Double.parseDouble(value));
                viewModel.setState(s);
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title);
        this.add(longInputField);
        this.add(latInputField);
        this.add(searchBtn);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("Click " + e.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final FindNearestRouteState state = (FindNearestRouteState) evt.getNewValue();
        setFields(state);
//        usernameErrorField.setText(state.getLoginError());
    }

    private void setFields(FindNearestRouteState state) {
        longInputField.setText(String.valueOf(state.getPosition().getLongitude()));
        latInputField.setText(String.valueOf(state.getPosition().getLatitude()));
    }

    public void setController(FindNearestRouteController controller) {
        this.controller = controller;
    }

    private static JFormattedTextField createDecimalField() {
        NumberFormat format = new DecimalFormat("#0.########################");

        InternationalFormatter formatter = new InternationalFormatter(format);
        formatter.setAllowsInvalid(false);
        return new JFormattedTextField(formatter);
    }

    private static void addDocumentListener(JFormattedTextField field,
                                           java.util.function.Consumer<String> callback) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { callback.accept(field.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { callback.accept(field.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { callback.accept(field.getText()); }
        });
    }
}
