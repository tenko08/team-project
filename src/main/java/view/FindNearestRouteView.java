package view;

import entities.BusStop;
import entities.Route;
import interface_adapter.ViewManagerModel;
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
    private final ViewManagerModel viewManagerModel;

    private final JTextField longInputField = new JTextField(15);
    private final JTextField latInputField = new JTextField(15);
    private final JLabel errorField = new JLabel("");
    private final JButton backButton = new JButton("← Back to Map");
    private final JButton homeButton = new JButton("Back");

    private final JTextArea outputArea = new JTextArea(6, 25);
    private FindNearestRouteController controller = null;
    // Geocoding
    private final JTextField addressField = new JTextField(25);
    private final JButton geocodeBtn = new JButton("Search address");
    private final DefaultListModel<Result> geocodeListModel = new DefaultListModel<>();
    private final JList<Result> geocodeResults = new JList<>(geocodeListModel);
    private final JLabel geocodeStatus = new JLabel("");
    private final NominatimGeocodingClient geocoder = new NominatimGeocodingClient();

    public FindNearestRouteView(ViewManagerModel viewManagerModel, FindNearestRouteViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        // === TOP BAR ===
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(backButton);
        topBar.add(homeButton);
        backButton.addActionListener(e -> {
            viewManagerModel.setState("map");
            viewManagerModel.firePropertyChange();
        });
        homeButton.addActionListener(e -> {
            viewManagerModel.setState("landing");
            viewManagerModel.firePropertyChange();
        });
        this.add(topBar, BorderLayout.NORTH);

        // content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        add(content, BorderLayout.CENTER);

        // Title
        JLabel title = new JLabel("Find Nearest Route");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        // address and coords
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(row);

        // address panel
        JPanel addrPanel = new JPanel();
        addrPanel.setLayout(new BoxLayout(addrPanel, BoxLayout.Y_AXIS));
        addrPanel.setBorder(BorderFactory.createTitledBorder("Address"));
        row.add(addrPanel);

        JPanel addrRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addrRow.add(addressField);
        addrRow.add(geocodeBtn);
        addrPanel.add(addrRow);

        geocodeResults.setVisibleRowCount(4);
        geocodeResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane geoScroll = new JScrollPane(geocodeResults);
        geoScroll.setPreferredSize(new Dimension(350, 80));
        addrPanel.add(geoScroll);

        geocodeStatus.setForeground(new Color(0, 102, 153));
        addrPanel.add(geocodeStatus);

        row.add(Box.createHorizontalStrut(15)); // spacing between sections

        // coord panel
        JPanel coordPanel = new JPanel();
        coordPanel.setLayout(new BoxLayout(coordPanel, BoxLayout.Y_AXIS));
        coordPanel.setBorder(BorderFactory.createTitledBorder("Coordinates"));
        row.add(coordPanel);

        // Latitude
        coordPanel.add(new JLabel("Latitude"));
//        latInputField.setMaximumSize(new Dimension(300, 28));
        coordPanel.add(latInputField);
        coordPanel.add(Box.createVerticalStrut(10));

        // Longitude
        coordPanel.add(new JLabel("Longitude"));
//        longInputField.setMaximumSize(new Dimension(300, 28));
        coordPanel.add(longInputField);
        coordPanel.add(Box.createVerticalStrut(10));

        // Error label
        errorField.setForeground(Color.RED);
        errorField.setAlignmentX(Component.LEFT_ALIGNMENT);
        coordPanel.add(errorField);
        coordPanel.add(Box.createVerticalStrut(10));

        // Search button
        JButton searchBtn = new JButton("Search");
        searchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        coordPanel.add(searchBtn);

        content.add(Box.createVerticalStrut(20));

        // Output area
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Result"));
        scrollPane.setPreferredSize(new Dimension(500, 180));
        content.add(scrollPane);

        searchBtn.addActionListener(e -> {
            FindNearestRouteState cur = viewModel.getState();
            controller.execute(cur.getPosition());
        });

        geocodeBtn.addActionListener(e -> performGeocode());
        geocodeResults.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Result r = geocodeResults.getSelectedValue();
                if (r != null) {
                    latInputField.setText(String.valueOf(r.getLatitude()));
                    longInputField.setText(String.valueOf(r.getLongitude()));

                    FindNearestRouteState s = viewModel.getState();
                    s.setLatitude(r.getLatitude());
                    s.setLongitude(r.getLongitude());
                    viewModel.setState(s);

                    errorField.setText("");
                }
            }
        });

        addDocumentListener(longInputField, v -> {
            validateInputs(searchBtn);
            if (errorField.getText().isEmpty() && !v.isBlank()) {
                FindNearestRouteState s = viewModel.getState();
                s.setLongitude(Double.parseDouble(v));
                viewModel.setState(s);
            }
        });

        addDocumentListener(latInputField, v -> {
            validateInputs(searchBtn);
            if (errorField.getText().isEmpty() && !v.isBlank()) {
                FindNearestRouteState s = viewModel.getState();
                s.setLatitude(Double.parseDouble(v));
                viewModel.setState(s);
            }
        });
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
                    // Unwrap root cause for clearer messaging
                    Throwable cause = ex;
                    if (ex.getCause() != null) {
                        cause = ex.getCause();
                        while (cause.getCause() != null) cause = cause.getCause();
                    }
                    String msg;
                    if (cause instanceof java.net.SocketTimeoutException) {
                        msg = "Address search timed out. Please check your internet connection and try again.";
                    } else if (cause instanceof java.io.IOException) {
                        msg = cause.getMessage() != null ? cause.getMessage() : "Network error during address search.";
                    } else {
                        msg = "Unexpected error during address search.";
                    }
                    geocodeStatus.setText(msg);
                } finally {
                    geocodeBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
