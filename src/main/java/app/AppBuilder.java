package app;

import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import interface_adapter.ViewManagerModel;
import view.MapView;
import view.ViewManager;
import use_case.map.MapOutputBoundary;

import interface_adapter.occupancy.OccupancyViewModel;
import view.OccupancyView;

import javax.swing.*;
import java.awt.*;

public class AppBuilder extends JFrame {
    private final Container cardPane = getContentPane();
    private final CardLayout cardLayout = new CardLayout();
    private static final String TITLE = "Real-Time TTC Map Viewer";
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPane, cardLayout, viewManagerModel);

    // DAO using file cache
    final CacheAccessObject cacheAccessObject = new CacheAccessObject();

    // For other views: declare view and view model, then implement methods to add
    // view and use case interactor
    private MapView mapView;

    // Occupancy components
    private OccupancyView occupancyView;
    private OccupancyViewModel occupancyViewModel;
    private interface_adapter.occupancy.OccupancyController occupancyController;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addMapView() {
        mapViewModel = new MapViewModel();
        mapView = new MapView(mapViewModel);
        cardPanel.add(mapView.getMapViewer(), mapView.getViewName());
        return this;
    }

    public AppBuilder addMapUseCase() {
        final MapOutputBoundary mapOutputBoundary = new MapPresenter(mapViewModel);
        final MapInputBoundary MapInteractor = new MapInteractor(cacheAccessObject, mapOutputBoundary);
        return this;
    }

    public AppBuilder addOccupancyView() {
        occupancyViewModel = new interface_adapter.occupancy.OccupancyViewModel();
        occupancyView = new OccupancyView(occupancyViewModel);
        cardPanel.add(occupancyView, occupancyView.getViewName());
        return this;
    }

    public AppBuilder addOccupancyUseCase() {
        final use_case.occupancy.OccupancyOutputBoundary occupancyOutputBoundary = new interface_adapter.occupancy.OccupancyPresenter(
                occupancyViewModel);
        final use_case.occupancy.OccupancyInputBoundary occupancyInteractor = new use_case.occupancy.OccupancyInteractor(
                occupancyOutputBoundary, cacheAccessObject);
        occupancyController = new interface_adapter.occupancy.OccupancyController(occupancyInteractor);
        return this;
    }

    // Helper to get controller for MainFrame
    public interface_adapter.occupancy.OccupancyController getOccupancyController() {
        return occupancyController;
    }

    public JFrame build() {
        final JFrame app = new JFrame(TITLE);
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setLayout(new BorderLayout());

        app.add(cardPanel, BorderLayout.CENTER);

        // Navigation Panel
        JPanel navPanel = new JPanel();
        JButton mapButton = new JButton("Map");
        JButton occupancyButton = new JButton("Occupancy");

        mapButton.addActionListener(e -> {
            viewManagerModel.setState(mapView.getViewName());
            viewManagerModel.firePropertyChange();
        });

        occupancyButton.addActionListener(e -> {
            viewManagerModel.setState(occupancyView.getViewName());
            viewManagerModel.firePropertyChange();
        });

        navPanel.add(mapButton);
        navPanel.add(occupancyButton);
        app.add(navPanel, BorderLayout.SOUTH);

        viewManagerModel.setState(mapView.getViewName()); // Default view
        viewManagerModel.firePropertyChange();

        return app;
    }
}
