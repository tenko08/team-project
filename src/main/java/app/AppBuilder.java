package app;

import interface_adapter.ViewManagerModel;
import view.MapView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;

public class AppBuilder extends JFrame {
    private final Container cardPane = getContentPane();
    private final CardLayout cardLayout = new CardLayout();
    private static final String TITLE = "Real-Time TTC Map Viewer";
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPane, cardLayout, viewManagerModel);

    private MapView mapView;

    public AppBuilder() { cardPane.setLayout(cardLayout); }

    public AppBuilder addMapView() {
        mapView = new MapView();
        cardPane.add(mapView, mapView.getViewName());
        return this;
    }

    public JFrame build() {
        final JFrame app = new JFrame(TITLE);
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        app.add(cardPane);

        viewManagerModel.setState(mapView.getViewName()); // Default view
        viewManagerModel.firePropertyChange();

        return app;
    }
}
