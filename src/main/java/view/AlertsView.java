package view;

import entities.Alert;
import interface_adapter.ViewManagerModel;
import interface_adapter.alerts.AlertsController;
import interface_adapter.alerts.AlertsViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AlertsView extends JPanel {
    private final String viewName = "alerts";
    private final AlertsViewModel viewModel;
    private final AlertsController controller;
    private final ViewManagerModel viewManagerModel;

    private final DefaultListModel<Alert> listModel = new DefaultListModel<>();
    private final JList<Alert> list = new JList<>(listModel);
    // Table view (optional) to make scanning easier
    private final JTable table = new JTable();
    private final javax.swing.table.DefaultTableModel tableModel =
            new javax.swing.table.DefaultTableModel(new Object[]{"Summary", "Effect", "routes.csv", "Stops"}, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
    private final JToggleButton toggleTableBtn = new JToggleButton("Table View");
    private final JPanel centerCards = new JPanel(new CardLayout());
    private final JButton refreshButton = new JButton("Refresh Alerts");
    private final JButton backButton = new JButton("← Back to Map");
    private final JButton homeButton = new JButton("Back");
    private final JLabel statusLabel = new JLabel(" ");

    public AlertsView(AlertsViewModel viewModel, AlertsController controller, ViewManagerModel viewManagerModel) {
        super(new BorderLayout());
        this.viewModel = viewModel;
        this.controller = controller;
        this.viewManagerModel = viewManagerModel;

        // Configure list renderer to highlight relevant alerts
        list.setCellRenderer(new AlertCellRenderer(viewModel));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(backButton);
        topBar.add(homeButton);
        topBar.add(refreshButton);
        topBar.add(toggleTableBtn);
        topBar.add(statusLabel);

        add(topBar, BorderLayout.NORTH);

        // List renderer to highlight relevant alerts and add spacing
        list.setCellRenderer(new AlertCellRenderer(viewModel));
        // Slightly larger font for readability
        list.setFont(list.getFont().deriveFont(list.getFont().getSize2D() + 0.0f));

        // Table setup
        table.setModel(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowSelectionAllowed(false);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, new AlertTableCellRenderer(viewModel));

        // Center uses cards to switch between list and table
        centerCards.add(new JScrollPane(list), "list");
        centerCards.add(new JScrollPane(table), "table");
        add(centerCards, BorderLayout.CENTER);

        backButton.addActionListener(e -> {
            viewManagerModel.setState("map");
            viewManagerModel.firePropertyChange();
        });

        homeButton.addActionListener(e -> {
            viewManagerModel.setState("landing");
            viewManagerModel.firePropertyChange();
        });

        refreshButton.addActionListener(e -> triggerRefresh());

        toggleTableBtn.addActionListener(e -> switchCenterCard());

        // Listen to view model updates
        viewModel.addPropertyChangeListener(evt -> SwingUtilities.invokeLater(this::render));

        // Initial state
        render();
    }

    public String getViewName() { return viewName; }

    private void triggerRefresh() {
        refreshButton.setEnabled(false);
        statusLabel.setText("Loading...");
        controller.execute();
    }

    private void render() {
        listModel.clear();
        List<Alert> alerts = viewModel.getAlerts();
        if (alerts != null) {
            for (Alert a : alerts) {
                listModel.addElement(a);
            }
        }

        // Rebuild table data too
        rebuildTable(alerts);

        int relevant = 0;
        String selectedRoute = viewModel.getSelectedRouteId();
        String selectedStop = viewModel.getSelectedStopId();
        if (alerts != null) {
            for (Alert a : alerts) {
                boolean routeMatch = selectedRoute != null && !selectedRoute.isBlank() && a.getRouteIds().contains(selectedRoute);
                boolean stopMatch = selectedStop != null && !selectedStop.isBlank() && a.getStopIds().contains(selectedStop);
                if (routeMatch || stopMatch) relevant++;
            }
        }

        if (viewModel.isLoading()) {
            statusLabel.setText("Loading...");
        } else if (!viewModel.isSuccess()) {
            String err = viewModel.getErrorMessage();
            statusLabel.setText(err == null || err.isBlank() ? "Failed to load alerts" : err);
        } else {
            if (selectedRoute != null || selectedStop != null) {
                statusLabel.setText(String.format("Loaded %d alert(s) (%d relevant)", listModel.size(), relevant));
            } else {
                statusLabel.setText(String.format("Loaded %d alert(s)", listModel.size()));
            }
        }

        refreshButton.setEnabled(!viewModel.isLoading());
    }

    private void switchCenterCard() {
        CardLayout cl = (CardLayout) centerCards.getLayout();
        cl.show(centerCards, toggleTableBtn.isSelected() ? "table" : "list");
    }

    private void rebuildTable(List<Alert> alerts) {
        tableModel.setRowCount(0);
        if (alerts == null) return;
        for (Alert a : alerts) {
            String summary = a.getDescriptionText() != null && !a.getDescriptionText().isBlank()
                    ? snippet(a.getDescriptionText())
                    : a.getHeaderText();
            // Use friendly mapping first; if blank/unknown, derive from text so Effect column is informative
            String effect = deriveFriendlyEffect(a);
            String routes = a.getRouteIds().isEmpty() ? "All routes" : String.join(", ", a.getRouteIds());
            String stops = a.getStopIds().isEmpty() ? "" : String.valueOf(a.getStopIds().size());
            tableModel.addRow(new Object[]{summary, effect, routes, stops});
        }
        // Set preferred column widths for readability
        if (table.getColumnModel().getColumnCount() >= 4) {
            table.getColumnModel().getColumn(0).setPreferredWidth(450);
            table.getColumnModel().getColumn(1).setPreferredWidth(140);
            table.getColumnModel().getColumn(2).setPreferredWidth(160);
            table.getColumnModel().getColumn(3).setPreferredWidth(80);
        }
    }

    private static String snippet(String text) {
        if (text == null) return "";
        return text.length() > 140 ? text.substring(0, 137) + "…" : text;
    }

    // Derive a simple, user-friendly effect even if GTFS effect is unknown, so table Effect isn't blank
    private static String deriveFriendlyEffect(Alert a) {
        String mapped = AlertCellRenderer.toFriendlyEffect(a.getEffect());
        if (mapped != null && !mapped.isBlank()) return mapped;

        StringBuilder sb = new StringBuilder();
        if (a.getDescriptionText() != null) sb.append(a.getDescriptionText()).append(' ');
        if (a.getHeaderText() != null) sb.append(a.getHeaderText());
        String lower = sb.toString().toLowerCase();

        if (lower.contains("detour")) return "Detour";
        if (lower.contains("proof of payment") || lower.contains("pop") || lower.contains("fare inspection") || lower.contains("inspection"))
            return "Proof of payment";
        if (lower.contains("no service") || lower.contains("suspended") || lower.contains("suspension") || lower.contains("shutdown"))
            return "No service";
        if (lower.contains("stop moved") || lower.contains("stop relocation") || lower.contains("relocated"))
            return "Stop moved";
        if (lower.contains("delay") || lower.contains("schedule change") || lower.contains("schedule changes") || lower.contains("diversion delay"))
            return "Delays / schedule changes";
        if (lower.contains("construction")) return "Construction";
        if (lower.contains("elevator") || lower.contains("escalator") || lower.contains("accessibility"))
            return "Accessibility";

        return "Advisory";
    }

    private static class AlertCellRenderer extends DefaultListCellRenderer {
        private final AlertsViewModel vm;

        public AlertCellRenderer(AlertsViewModel vm) {
            this.vm = vm;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Alert a = (Alert) value;

            // Build friendly effect label, hide if unknown/empty
            String effectFriendly = toFriendlyEffect(a.getEffect());

            // Label the numbers explicitly as route numbers
            String routesPart = a.getRouteIds().isEmpty()
                    ? "All routes"
                    : "routes.csv: " + String.join(", ", a.getRouteIds());

            String stopsPart = a.getStopIds().isEmpty()
                    ? ""
                    : "Stops affected: " + a.getStopIds().size();

            StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            // Show only one primary text line to avoid repetition:
            // Prefer the full description (snippet) if available; otherwise show the header.
            String desc = a.getDescriptionText();
            if (desc != null && !desc.isBlank()) {
                String snippet = desc.length() > 140 ? desc.substring(0, 137) + "…" : desc;
                sb.append(escape(snippet));
            } else {
                // Header (bold) when no description is available
                sb.append("<b>").append(escape(a.getHeaderText())).append("</b>");
            }
            // Effect line if meaningful
            if (!effectFriendly.isBlank()) {
                sb.append("<br/>").append(escape(effectFriendly));
            }
            // Route and stop info
            sb.append("<br/>").append(escape(routesPart));
            if (!stopsPart.isBlank()) sb.append(" &nbsp;·&nbsp; ").append(escape(stopsPart));
            sb.append("</html>");

            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, sb.toString(), index, isSelected, cellHasFocus);
            // Provide full description as tooltip for clarity
            if (a.getDescriptionText() != null && !a.getDescriptionText().isBlank()) {
                lbl.setToolTipText(escape(a.getDescriptionText()));
            } else {
                lbl.setToolTipText(null);
            }

            String route = vm.getSelectedRouteId();
            String stop = vm.getSelectedStopId();
            boolean relevant = (route != null && !route.isBlank() && a.getRouteIds().contains(route))
                    || (stop != null && !stop.isBlank() && a.getStopIds().contains(stop));

            if (relevant) {
                lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                if (!isSelected) {
                    lbl.setBackground(new Color(255, 250, 205)); // light gold
                    lbl.setOpaque(true);
                }
            }
            // Add padding and a subtle bottom divider to create space between alerts
            int pad = 8;
            javax.swing.border.Border padding = BorderFactory.createEmptyBorder(pad, pad, pad, pad);
            javax.swing.border.Border bottomLine = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230));
            lbl.setBorder(BorderFactory.createCompoundBorder(bottomLine, padding));
            return lbl;
        }

        private static String toFriendlyEffect(String effectRaw) {
            if (effectRaw == null) return "";
            String e = effectRaw.trim().toUpperCase();
            switch (e) {
                case "NO_SERVICE":
                    return "No service";
                case "REDUCED_SERVICE":
                    return "Reduced service";
                case "SIGNIFICANT_DELAYS":
                case "MODIFIED_SERVICE":
                case "SCHEDULE_CHANGE":
                case "DELAY":
                case "EXPECTED_DELAYS":
                    return "Delays / schedule changes";
                case "DETOUR":
                    return "Detour in effect";
                case "ADDITIONAL_SERVICE":
                    return "Additional service";
                case "STOP_MOVED":
                    return "Stop moved";
                case "OTHER_EFFECT":
                    return "Advisory";
                case "UNKNOWN_EFFECT":
                default:
                    return ""; // hide unknown/unmapped effects
            }
        }

        private static String escape(String s) {
            if (s == null) return "";
            return s.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
        }
    }

    // Table renderer to mirror relevant highlight
    private static class AlertTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        private final AlertsViewModel vm;
        public AlertTableCellRenderer(AlertsViewModel vm) { this.vm = vm; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            // Determine relevance using the underlying list model if available
            // We assume row indices align with current alerts list order built in rebuildTable
            if (!isSelected && vm != null && vm.getAlerts() != null && row < vm.getAlerts().size()) {
                Alert a = vm.getAlerts().get(row);
                String route = vm.getSelectedRouteId();
                String stop = vm.getSelectedStopId();
                boolean relevant = (route != null && !route.isBlank() && a.getRouteIds().contains(route))
                        || (stop != null && !stop.isBlank() && a.getStopIds().contains(stop));
                if (relevant) {
                    c.setBackground(new Color(255, 250, 205));
                } else {
                    c.setBackground(Color.WHITE);
                }
            }
            return c;
        }
    }
}
