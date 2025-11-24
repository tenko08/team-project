package app;

import data_access.CacheAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.alerts.AlertsController;
import interface_adapter.alerts.AlertsPresenter;
import interface_adapter.alerts.AlertsViewModel;
import interface_adapter.map.MapPresenter;
import interface_adapter.map.MapViewModel;
import use_case.alerts.AlertsInputBoundary;
import use_case.alerts.AlertsInteractor;
import use_case.alerts.AlertsOutputBoundary;
import use_case.map.MapInputBoundary;
import use_case.map.MapInteractor;
import view.MapView;
import view.ViewManager;
import use_case.map.MapOutputBoundary;
import view.AlertsView;
import api.AlertDataBaseAPI;

import javax.swing.*;
import java.awt.*;

public class AppBuilder extends JFrame {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private static final String TITLE = "Real-Time TTC Map Viewer";
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // DAO using file cache
    final CacheAccessObject cacheAccessObject = new CacheAccessObject();

    // For other views: declare view and view model, then implement methods to add view and use case interactor
    private MapView mapView;
    private MapViewModel mapViewModel;

    private AlertsView alertsView;
    private AlertsViewModel alertsViewModel;
    private AlertsController alertsController;

    public AppBuilder() { cardPanel.setLayout(cardLayout); }

    public AppBuilder addMapView() {
        mapViewModel = new MapViewModel();
        mapView = new MapView(mapViewModel);
        cardPanel.add(mapView, mapView.getViewName());
        return this;
    }

    public AppBuilder addMapUseCase() {
        final MapOutputBoundary mapOutputBoundary = new MapPresenter(mapViewModel);
        final MapInputBoundary MapInteractor = new MapInteractor(cacheAccessObject, mapOutputBoundary);
        return this;
    }

    public AppBuilder addAlertsUseCase() {
        alertsViewModel = new AlertsViewModel();
        final AlertsOutputBoundary presenter = new AlertsPresenter(alertsViewModel);
        final AlertsInputBoundary interactor = new AlertsInteractor(new AlertDataBaseAPI(), presenter);
        alertsController = new AlertsController(interactor);
        return this;
    }

    public AppBuilder addAlertsView() {
        if (alertsViewModel == null || alertsController == null) {
            // Ensure use case is initialized before view
            addAlertsUseCase();
        }
        alertsView = new AlertsView(alertsViewModel, alertsController, viewManagerModel);
        cardPanel.add(alertsView, alertsView.getViewName());
        return this;
    }

    public JFrame build() {
        final JFrame app = new JFrame(TITLE);
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create a container with BorderLayout to host toolbar + cards
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildToolbar(), BorderLayout.NORTH);
        root.add(cardPanel, BorderLayout.CENTER);

        app.setContentPane(root);

        viewManagerModel.setState(mapView.getViewName()); // Default view
        viewManagerModel.firePropertyChange();

        return app;
    }

    private JComponent buildToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton alertsBtn = new JButton("Alerts");
        alertsBtn.addActionListener(e -> {
            // Navigate to Alerts view and trigger refresh
            viewManagerModel.setState("alerts");
            viewManagerModel.firePropertyChange();
            if (alertsController != null && alertsViewModel != null) {
                alertsViewModel.setLoading(true);
                alertsViewModel.firePropertyChanged();
                alertsController.execute();
            }
        });

        toolBar.add(alertsBtn);
        return toolBar;
    }
}
