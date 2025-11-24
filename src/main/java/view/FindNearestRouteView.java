package view;

import entities.BusStop;
import entities.Route;
import interface_adapter.find_nearest_route.FindNearestRouteController;
import api.NominatimGeocodingClient;
import api.NominatimGeocodingClient.Result;
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

    private final JTextArea outputArea = new JTextArea(6, 25);
    private FindNearestRouteController controller = null;
    // Geocoding
    private final JTextField addressField = new JTextField(25);
    private final JButton geocodeBtn = new JButton("Search address");
    private final DefaultListModel<Result> geocodeListModel = new DefaultListModel<>();
    private final JList<Result> geocodeResults = new JList<>(geocodeListModel);
    private final JLabel geocodeStatus = new JLabel("");
    private final NominatimGeocodingClient geocoder = new NominatimGeocodingClient();

    public FindNearestRouteView(FindNearestRouteViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Find Nearest Route");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        this.add(title);
        this.add(Box.createVerticalStrut(15));


        // Address to coordinate
        JPanel addrPanel = new JPanel();
        addrPanel.setLayout(new BoxLayout(addrPanel, BoxLayout.Y_AXIS));
        JLabel addrLabel = new JLabel("Address");
        addrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        addrPanel.add(addrLabel);
        JPanel addrRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        addrRow.add(addressField);
        addrRow.add(geocodeBtn);
        addrPanel.add(addrRow);
        geocodeResults.setVisibleRowCount(4);
        geocodeResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane geoScroll = new JScrollPane(geocodeResults);
        geoScroll.setPreferredSize(new Dimension(500, 80));
        addrPanel.add(geoScroll);
        geocodeStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        geocodeStatus.setForeground(new Color(0, 102, 153));
        addrPanel.add(geocodeStatus);
        this.add(addrPanel);
        this.add(Box.createVerticalStrut(10));

        JLabel lonLabel = new JLabel("---Longitude---");
        lonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(lonLabel);
        longInputField.setMaximumSize(new Dimension(300, 30));
        this.add(longInputField);
        this.add(Box.createVerticalStrut(10));

        JLabel latLabel = new JLabel("---Latitude---");
        latLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(latLabel);
        latInputField.setMaximumSize(new Dimension(300, 30));
        this.add(latInputField);
        this.add(Box.createVerticalStrut(10));

        errorField.setForeground(Color.RED);
        errorField.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorField.setPreferredSize(new Dimension(300, 20));
        errorField.setMinimumSize(new Dimension(300, 20));
        errorField.setMaximumSize(new Dimension(300, 20));
        this.add(errorField);
        this.add(Box.createVerticalStrut(10));

        JButton searchBtn = new JButton("Search");
        searchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(searchBtn);
        this.add(Box.createVerticalStrut(20));
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(searchBtn)) {
                    final FindNearestRouteState curState = viewModel.getState();
                    controller.execute(curState.getPosition());
                }
            }
        });

        geocodeBtn.addActionListener(e -> performGeocode());
        geocodeResults.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Result r = geocodeResults.getSelectedValue();
                if (r != null) {
                    latInputField.setText(String.valueOf(r.getLatitude()));
                    longInputField.setText(String.valueOf(r.getLongitude()));
                    // update state
                    FindNearestRouteState s = viewModel.getState();
                    s.setLatitude(r.getLatitude());
                    s.setLongitude(r.getLongitude());
                    viewModel.setState(s);
                    errorField.setText("");
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

        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Result"));
        this.add(scrollPane);

//        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//
//        this.add(title);
//        this.add(longInputField);
//        this.add(latInputField);
//        this.add(errorField);
//        this.add(searchBtn);
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
        BusStop bs = state.getBusStop();
        Route route = state.getRoute();

        if (bs != null && route != null) {
            String formatted =
                    "Nearest Route Information\n" +
                            "-----------------------------------\n" +
                            "Route Number: " + route.getRouteNumber() + "\n" +
                            "Stops on Route: " + route.getBusStopList().size() + "\n" +
                            "\n" +
                            "Nearest Bus Stop\n" +
                            "-----------------------------------\n" +
                            "Stop ID: " + bs.getId() + "\n" +
                            "Name: " + bs.getName() + "\n" +
                            "Sequence on Route: " + bs.getStopSequence() + "\n" +
                            "Latitude: " + bs.getPosition().getLatitude() + "\n" +
                            "Longitude: " + bs.getPosition().getLongitude();

            outputArea.setText(formatted);
        } else {
            outputArea.setText("No route or bus stop found.");
        }
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

    private void performGeocode() {
        String q = addressField.getText().trim();
        if (q.isEmpty()) {
            geocodeStatus.setText("Enter an address to search.");
            return;
        }
        geocodeBtn.setEnabled(false);
        geocodeStatus.setText("Searching...");
        geocodeListModel.clear();

        SwingWorker<java.util.List<Result>, Void> worker = new SwingWorker<>() {
            @Override
            protected java.util.List<Result> doInBackground() throws Exception {
                return geocoder.search(q, 5);
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Result> results = get();
                    for (Result r : results) geocodeListModel.addElement(r);
                    geocodeStatus.setText(results.isEmpty() ? "No results found." : "Select a result to use its coordinates.");
                } catch (Exception ex) {
                    geocodeStatus.setText("Search failed: " + ex.getMessage());
                } finally {
                    geocodeBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
