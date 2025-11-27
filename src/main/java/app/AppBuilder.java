package app;

import api.AlertDataBaseAPI;
import api.BusDataBaseAPI;
import data_access.BusDataAccessObject;
import data_access.CacheAccessObject;
import entities.*;
import interface_adapter.ViewManagerModel;
import interface_adapter.alerts.AlertsController;
import interface_adapter.alerts.AlertsPresenter;
import interface_adapter.alerts.AlertsViewModel;
import interface_adapter.find_nearest_route.FindNearestRouteController;
import interface_adapter.find_nearest_route.FindNearestRoutePresenter;
import interface_adapter.find_nearest_route.FindNearestRouteViewModel;
import interface_adapter.map.MapPresenter;
import interface_adapter.map.MapViewModel;
import interface_adapter.occupancy.OccupancyController;
import interface_adapter.occupancy.OccupancyPresenter;
import interface_adapter.occupancy.OccupancyViewModel;
import interface_adapter.search_by_route.SearchByRouteController;
import interface_adapter.search_by_route.SearchByRouteGateway;
import interface_adapter.search_by_route.SearchByRouteGatewayImpl;
import interface_adapter.search_by_route.SearchByRoutePresenter;
import interface_adapter.search_by_route.SearchByRouteViewModel;
import use_case.alerts.AlertsInputBoundary;
import use_case.alerts.AlertsInteractor;
import use_case.alerts.AlertsOutputBoundary;
import use_case.find_nearest_route.FindNearestRouteDataAccessInterface;
import use_case.find_nearest_route.FindNearestRouteInputBoundary;
import use_case.find_nearest_route.FindNearestRouteInteractor;
import use_case.find_nearest_route.FindNearestRouteOutputBoundary;
import use_case.map.MapInputBoundary;
import use_case.map.MapInteractor;
import use_case.map.MapOutputBoundary;
import use_case.occupancy.OccupancyInputBoundary;
import use_case.occupancy.OccupancyInteractor;
import use_case.occupancy.OccupancyOutputBoundary;
import use_case.search_by_route.SearchByRouteInteractor;
import view.AlertsView;
import view.FindNearestRouteView;
import view.MapView;
import view.OccupancyView;
import view.SearchByRouteView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class AppBuilder extends JFrame {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private static final String TITLE = "Real-Time TTC Map Viewer";
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // DAO using file cache
    final CacheAccessObject cacheAccessObject = new CacheAccessObject();
    final BusDataAccessObject busDataAccessObject = new BusDataAccessObject();

    // For other views: declare view and view model, then implement methods to add
    // view and use case interactor
    private MapView mapView;
    private MapViewModel mapViewModel;
    private FindNearestRouteView findNearestRouteView;
    private FindNearestRouteViewModel findNearestRouteViewModel;

    private AlertsView alertsView;
    private AlertsViewModel alertsViewModel;
    private AlertsController alertsController;

    private SearchByRouteView searchByRouteView;
    private SearchByRouteViewModel searchByRouteViewModel;
    private SearchByRouteController searchByRouteController;

    private OccupancyView occupancyView;
    private OccupancyViewModel occupancyViewModel;
    private OccupancyController occupancyController;

    private final String[] themeList = {
            // UIManager.getSystemLookAndFeelClassName(),
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
            "javax.swing.plaf.metal.MetalLookAndFeel",
            "javax.swing.plaf.nimbus.NimbusLookAndFeel",
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
            "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"
    };

    private int currentThemeIndex = 0;

    public AppBuilder() {
        setTheme(0);
        cardPanel.setLayout(cardLayout);
    }

    public void setTheme(int themeIndex) {
        try {
            UIManager.setLookAndFeel(themeList[themeIndex]);

            for (Frame frame : Frame.getFrames()) {
                updateLAFRecursively(frame);
            }
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException
                | ClassNotFoundException e) {
            System.out.println("Error loading theme: " + e);
        }
    }

    public static void updateLAFRecursively(Window window) {
        for (Window childWindow : window.getOwnedWindows()) {
            updateLAFRecursively(childWindow);
        }
        SwingUtilities.updateComponentTreeUI(window);
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

    public AppBuilder addFindNearestRouteView() {
        findNearestRouteViewModel = new FindNearestRouteViewModel();
        findNearestRouteView = new FindNearestRouteView(viewManagerModel, findNearestRouteViewModel);
        cardPanel.add(findNearestRouteView, findNearestRouteView.getViewName());
        return this;
    }

    public AppBuilder addFindNearestRouteUseCase() {
        final FindNearestRouteOutputBoundary findNearestRouteOutputBoundary = new FindNearestRoutePresenter(
                findNearestRouteViewModel);
        // TODO: use DAO, this is tempdata
        final FindNearestRouteInputBoundary findNearestRouteInteractor = new FindNearestRouteInteractor(
                busDataAccessObject
                // new FindNearestRouteDataAccessInterface() {
                // @Override
                // public List<Route> getAllRoutes() {
                // List<Route> routes = new ArrayList<Route>();
                // Route route929 = new Route(929);
                // route929.addBusStop(new BusStop(1526, 16, "Victoria Park Ave at Navaho Dr",
                // new Position(43.800546, -79.334889)));
                //
                //// route929.addTrip(new Trip(72598070, route929, new Bus(9446,
                //// new Position(43.65386, -79.43306, 164, 0),
                //// "FEW_SEATS_AVAILABLE")));
                ////
                //// route929.addTrip(new Trip(76422070, route929, new Bus(9432,
                //// new Position(43.7322, -79.45838, 253, 0),
                //// "EMPTY")));
                //
                // routes.add(route929);
                // return routes;
                // }
                // }
                , findNearestRouteOutputBoundary);

        FindNearestRouteController findNearestRouteController = new FindNearestRouteController(
                findNearestRouteInteractor);
        findNearestRouteView.setController(findNearestRouteController);
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

    public AppBuilder addSearchByRouteUseCase() {
        searchByRouteViewModel = new SearchByRouteViewModel();
        SearchByRouteGateway gateway = new SearchByRouteGatewayImpl(new BusDataBaseAPI());
        SearchByRoutePresenter presenter = new SearchByRoutePresenter(searchByRouteViewModel);
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);
        searchByRouteController = new SearchByRouteController(interactor);
        return this;
    }

    public AppBuilder addSearchByRouteView() {
        if (searchByRouteController == null || searchByRouteViewModel == null) {
            addSearchByRouteUseCase();
        }
        searchByRouteView = new SearchByRouteView(searchByRouteController, searchByRouteViewModel, viewManagerModel);
        cardPanel.add(searchByRouteView, searchByRouteView.getViewName());
        return this;
    }

    public AppBuilder addOccupancyUseCase() {
        occupancyViewModel = new OccupancyViewModel();
        final OccupancyOutputBoundary presenter = new OccupancyPresenter(occupancyViewModel);
        final OccupancyInputBoundary interactor = new OccupancyInteractor(new BusDataBaseAPI(), presenter);
        occupancyController = new OccupancyController(interactor);
        return this;
    }

    public AppBuilder addOccupancyView() {
        if (occupancyViewModel == null || occupancyController == null) {
            // Ensure use case is initialized before view
            addOccupancyUseCase();
        }
        occupancyView = new OccupancyView(occupancyViewModel, viewManagerModel);
        occupancyView.setController(occupancyController);
        cardPanel.add(occupancyView, occupancyView.getViewName());
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
        // viewManagerModel.setState(findNearestRouteView.getViewName());
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

        JButton findNearestRouteBtn = new JButton("Find Nearest Route");
        findNearestRouteBtn.addActionListener(e -> {
            viewManagerModel.setState(findNearestRouteView.getViewName());
            viewManagerModel.firePropertyChange();
        });

        JButton searchByRouteBtn = new JButton("Search by Route");
        searchByRouteBtn.addActionListener(e -> {
            if (searchByRouteView != null) {
                viewManagerModel.setState(searchByRouteView.getViewName());
                viewManagerModel.firePropertyChange();
            }
        });

        JButton occupancyBtn = new JButton("Occupancy");
        occupancyBtn.addActionListener(e -> {
            if (occupancyView != null) {
                viewManagerModel.setState(occupancyView.getViewName());
                viewManagerModel.firePropertyChange();
            }
        });

        toolBar.add(alertsBtn);
        toolBar.add(findNearestRouteBtn);
        toolBar.add(searchByRouteBtn);
        toolBar.add(occupancyBtn);

        toolBar.add(Box.createHorizontalGlue());

        JLabel themeNum = new JLabel((currentThemeIndex + 1) + "/" + themeList.length);
        themeNum.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JButton changeThemeBtn = new JButton("Change Theme");
        changeThemeBtn.addActionListener(e -> {
            currentThemeIndex = (currentThemeIndex + 1) % themeList.length;
            themeNum.setText((currentThemeIndex + 1) + "/" + themeList.length);
            setTheme(currentThemeIndex);

        });
        toolBar.add(changeThemeBtn);
        toolBar.add(themeNum);

        return toolBar;
    }
}
