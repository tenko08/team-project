package app;

import data_access.CacheAccessObject;
import entities.*;
import interface_adapter.ViewManagerModel;
import interface_adapter.find_nearest_route.FindNearestRouteController;
import interface_adapter.find_nearest_route.FindNearestRoutePresenter;
import interface_adapter.find_nearest_route.FindNearestRouteViewModel;
import interface_adapter.map.MapPresenter;
import interface_adapter.map.MapViewModel;
import use_case.find_nearest_route.FindNearestRouteDataAccessInterface;
import use_case.find_nearest_route.FindNearestRouteInputBoundary;
import use_case.find_nearest_route.FindNearestRouteInteractor;
import use_case.find_nearest_route.FindNearestRouteOutputBoundary;
import use_case.map.MapInputBoundary;
import use_case.map.MapInteractor;
import view.FindNearestRouteView;
import view.MapView;
import view.ViewManager;
import use_case.map.MapOutputBoundary;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
    private FindNearestRouteView findNearestRouteView;
    private FindNearestRouteViewModel findNearestRouteViewModel;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addMapView() {
        mapViewModel = new MapViewModel();
        mapView = new MapView(mapViewModel);
        cardPanel.add(mapView.getMapViewer(),mapView.getViewName());
        return this;
    }

    public AppBuilder addMapUseCase() {
        final MapOutputBoundary mapOutputBoundary = new MapPresenter(mapViewModel);
        final MapInputBoundary MapInteractor = new MapInteractor(cacheAccessObject, mapOutputBoundary);
        return this;
    }

    public AppBuilder addFindNearestRouteView() {
        findNearestRouteViewModel = new FindNearestRouteViewModel();
        findNearestRouteView = new FindNearestRouteView(findNearestRouteViewModel);
        cardPanel.add(findNearestRouteView,findNearestRouteView.getViewName());
        return this;
    }

    public AppBuilder addFindNearestRouteUseCase() {
        final FindNearestRouteOutputBoundary findNearestRouteOutputBoundary
                = new FindNearestRoutePresenter(findNearestRouteViewModel);
        // TODO: use DAO, this is tempdata
        final FindNearestRouteInputBoundary findNearestRouteInteractor
                = new FindNearestRouteInteractor(new FindNearestRouteDataAccessInterface() {
            @Override
            public List<Route> getAllRoutes() {
                List<Route> routes = new ArrayList<Route>();
                Route route929 = new Route(929);
                route929.addBusStop(new BusStop(1526, 16, "Victoria Park Ave at Navaho Dr",
                        new Position(43.800546, -79.334889)));

                route929.addTrip(new Trip(72598070, route929, new Bus(9446,
                        new Position(43.65386, -79.43306, 164, 0),
                        "FEW_SEATS_AVAILABLE")));

                route929.addTrip(new Trip(76422070, route929, new Bus(9432,
                        new Position(43.7322, -79.45838, 253, 0),
                        "EMPTY")));

                routes.add(route929);
                return routes;
            }
        }, findNearestRouteOutputBoundary);

        FindNearestRouteController findNearestRouteController
                = new FindNearestRouteController(findNearestRouteInteractor);
        findNearestRouteView.setController(findNearestRouteController);
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
//        viewManagerModel.setState(findNearestRouteView.getViewName());
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
//            if (alertsController != null && alertsViewModel != null) {
//                alertsViewModel.setLoading(true);
//                alertsViewModel.firePropertyChanged();
//                alertsController.execute();
//            }
        });

        JButton findNearestRouteBtn = new JButton("Find Nearest Route");
        findNearestRouteBtn.addActionListener(e -> {
            viewManagerModel.setState(findNearestRouteView.getViewName());
            viewManagerModel.firePropertyChange();
        });

        toolBar.add(alertsBtn);
        toolBar.add(findNearestRouteBtn);
        return toolBar;
    }
}
