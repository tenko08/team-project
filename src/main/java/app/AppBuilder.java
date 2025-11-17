package app;

import data_access.CacheAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.map.MapPresenter;
import interface_adapter.map.MapViewModel;
import use_case.map.MapInputBoundary;
import use_case.map.MapInteractor;
import view.MapView;
import view.ViewManager;
import use_case.map.MapOutputBoundary;

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

    public AppBuilder() { cardPanel.setLayout(cardLayout); }

    public AppBuilder addMapView() {
        mapViewModel = new MapViewModel();
        mapView = new MapView(mapViewModel);
        cardPanel.add(mapView.getMapViewer());
        return this;
    }

    public AppBuilder addMapUseCase() {
        final MapOutputBoundary mapOutputBoundary = new MapPresenter(mapViewModel);
        final MapInputBoundary MapInteractor = new MapInteractor(cacheAccessObject, mapOutputBoundary);
        return this;
    }

    public JFrame build() {
        final JFrame app = new JFrame(TITLE);
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        app.setContentPane(cardPanel);

        viewManagerModel.setState(mapView.getViewName()); // Default view
        viewManagerModel.firePropertyChange();

        return app;
    }
}
