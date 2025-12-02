package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import api.AlertDataBaseAPI;
import api.BusDataBaseAPI;
import data_access.BusDataAccessObject;
import data_access.CacheAccessObject;
import data_access.RouteShapeDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.alerts.AlertsController;
import interface_adapter.alerts.AlertsPresenter;
import interface_adapter.alerts.AlertsViewModel;
import interface_adapter.bus_schedule_eta.BusScheduleController;
import interface_adapter.bus_schedule_eta.BusScheduleGateway;
import interface_adapter.bus_schedule_eta.BusScheduleGatewayImpl;
import interface_adapter.bus_schedule_eta.BusSchedulePresenter;
import interface_adapter.bus_schedule_eta.BusScheduleViewModel;
import interface_adapter.find_nearest_route.FindNearestRouteController;
import interface_adapter.find_nearest_route.FindNearestRoutePresenter;
import interface_adapter.find_nearest_route.FindNearestRouteViewModel;
import interface_adapter.map.MapController;
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
import use_case.bus_schedule_eta.BusScheduleInputBoundary;
import use_case.bus_schedule_eta.BusScheduleInteractor;
import use_case.bus_schedule_eta.BusScheduleOutputBoundary;
import use_case.find_nearest_route.FindNearestRouteInputBoundary;
import use_case.find_nearest_route.FindNearestRouteInteractor;
import use_case.find_nearest_route.FindNearestRouteOutputBoundary;
import use_case.map.*;
import use_case.occupancy.OccupancyInputBoundary;
import use_case.occupancy.OccupancyInteractor;
import use_case.occupancy.OccupancyOutputBoundary;
import use_case.search_by_route.SearchByRouteInputBoundary;
import use_case.search_by_route.SearchByRouteInteractor;
import view.AlertsView;
import view.BusScheduleView;
import view.FindNearestRouteView;
import view.MapView;
import view.LandingView;
import view.OccupancyView;
import view.SearchByRouteView;
import view.ViewManager;

public class AppBuilder extends JFrame {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private static final String TITLE = "Real-Time TTC Map Viewer";
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // DAO using file cache
    final MapDataAccessInterface cacheAccessObject = new CacheAccessObject();
    final BusDataAccessObject busDataAccessObject = new BusDataAccessObject();
    final RouteShapeDataAccessInterface routeShapeDataAccessInterface = new RouteShapeDataAccessObject();

    // For other views: declare view and view model, then implement methods to add
    // view and use case interactor
    private MapView mapView;
    private MapViewModel mapViewModel;
    private MapInputBoundary mapInteractor;
    private FindNearestRouteView findNearestRouteView;
    private FindNearestRouteViewModel findNearestRouteViewModel;
    private LandingView landingView;

    private AlertsView alertsView;
    private AlertsViewModel alertsViewModel;
    private AlertsController alertsController;

    private SearchByRouteView searchByRouteView;
    private SearchByRouteViewModel searchByRouteViewModel;
    private SearchByRouteController searchByRouteController;
    private SearchByRouteInputBoundary searchByRouteInputBoundary;

    private BusScheduleView busScheduleView;
    private BusScheduleViewModel busScheduleViewModel;
    private BusScheduleController busScheduleController;
    private OccupancyView occupancyView;
    private OccupancyViewModel occupancyViewModel;
    private OccupancyController occupancyController;

    Properties prop = new Properties();

    // Reordered so that previous "2/5" theme (Metal) is now first and used as default
    private final String[] themeList = {
            // "2/5" → Metal is now the first (default) theme
            "javax.swing.plaf.metal.MetalLookAndFeel",
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
            "javax.swing.plaf.nimbus.NimbusLookAndFeel",
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
            "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"
    };

    private int currentThemeIndex = 0; // Default points to the first theme (now Metal)

    public AppBuilder() {
        try {
            Scanner sc = new Scanner(new File("themeConfig.txt"));
            currentThemeIndex = sc.nextInt();
            sc.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Config file not found");
        }

        setTheme(currentThemeIndex);
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
        mapOutputBoundary.addWaypointChangeListener(mapView);
        mapInteractor = new MapInteractor(cacheAccessObject, busDataAccessObject, routeShapeDataAccessInterface,
                mapOutputBoundary);

        MapController controller = new MapController(mapInteractor);
        mapView.setMapController(controller);
        return this;
    }

    public AppBuilder addFindNearestRouteView() {
        findNearestRouteViewModel = new FindNearestRouteViewModel();
        findNearestRouteView = new FindNearestRouteView(viewManagerModel, findNearestRouteViewModel);
        cardPanel.add(findNearestRouteView, findNearestRouteView.getViewName());
        return this;
    }

    public AppBuilder addLandingView() {
        // Ensure dependencies exist
        if (mapView == null) {
            addMapView();
        }
        if (findNearestRouteView == null) {
            addFindNearestRouteView();
        }
        landingView = new LandingView(viewManagerModel,
                findNearestRouteView.getViewName(),
                mapView.getViewName());
        cardPanel.add(landingView, landingView.getViewName());
        return this;
    }

    public AppBuilder addFindNearestRouteUseCase() {
        final FindNearestRouteOutputBoundary findNearestRouteOutputBoundary = new FindNearestRoutePresenter(
                findNearestRouteViewModel);
        // TODO: use DAO, this is tempdata
        final FindNearestRouteInputBoundary findNearestRouteInteractor = new FindNearestRouteInteractor(
                busDataAccessObject, findNearestRouteOutputBoundary, searchByRouteInputBoundary);
        mapInteractor.setFindNearestRouteOutputBoundary(findNearestRouteOutputBoundary);

        FindNearestRouteController findNearestRouteController
                = new FindNearestRouteController(findNearestRouteInteractor);
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
                // , findNearestRouteOutputBoundary);

        findNearestRouteView.setController(findNearestRouteController);
        return this;
    }

    public AppBuilder addAlertsUseCase() {
        alertsViewModel = new AlertsViewModel();
        final AlertsOutputBoundary presenter = new AlertsPresenter(alertsViewModel);
        final AlertsInputBoundary interactor = new AlertsInteractor(new AlertDataBaseAPI(), presenter,
                searchByRouteInputBoundary);
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
        SearchByRouteGateway gateway = new SearchByRouteGatewayImpl(new BusDataAccessObject());
        SearchByRoutePresenter presenter = new SearchByRoutePresenter(searchByRouteViewModel);
        searchByRouteInputBoundary = new SearchByRouteInteractor(gateway, presenter,
                routeShapeDataAccessInterface, mapInteractor);
        searchByRouteController = new SearchByRouteController(searchByRouteInputBoundary);
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

    public AppBuilder addBusScheduleUseCase() {
        busScheduleViewModel = new BusScheduleViewModel();
        BusScheduleGateway busScheduleGateway = new BusScheduleGatewayImpl(new BusDataBaseAPI());
        final BusScheduleOutputBoundary presenter = new BusSchedulePresenter(busScheduleViewModel);
        final BusScheduleInputBoundary interactor = new BusScheduleInteractor(busScheduleGateway, presenter,
                searchByRouteInputBoundary);
        busScheduleController = new BusScheduleController(interactor);
        return this;
    }

    public AppBuilder addBusScheduleView() {
        if (busScheduleController == null || busScheduleViewModel == null) {
            addBusScheduleUseCase();
        }
        busScheduleView = new BusScheduleView(busScheduleController, busScheduleViewModel);
        cardPanel.add(busScheduleView, busScheduleView.getViewName());
        return this;
    }
    
    public AppBuilder addOccupancyUseCase() {
        occupancyViewModel = new OccupancyViewModel();
        final OccupancyOutputBoundary presenter = new OccupancyPresenter(occupancyViewModel);
        final OccupancyInputBoundary interactor = new OccupancyInteractor(new BusDataBaseAPI(), presenter,
                searchByRouteInputBoundary);
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
        app.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Create a container with BorderLayout to host toolbar + cards
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildToolbar(), BorderLayout.NORTH);
        root.add(cardPanel, BorderLayout.CENTER);

        app.setContentPane(root);

        // Default to landing page if available; otherwise fallback to map
        if (landingView != null) {
            viewManagerModel.setState(landingView.getViewName());
        } else {
            viewManagerModel.setState(mapView.getViewName());
        }
        // viewManagerModel.setState(findNearestRouteView.getViewName());
        viewManagerModel.firePropertyChange();

        return app;
    }

    private JComponent buildToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

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

        JButton searchBusETABtn = new JButton("Search Bus ETA");
        searchBusETABtn.addActionListener(e -> {
            System.out.println("Search Bus ETA button clicked");
             viewManagerModel.setState(busScheduleView.getViewName());
             viewManagerModel.firePropertyChange();
        });

        JButton occupancyBtn = new JButton("Occupancy");
        occupancyBtn.addActionListener(e -> {
            if (occupancyView != null) {
                viewManagerModel.setState(occupancyView.getViewName());
                viewManagerModel.firePropertyChange();
            }
        });

        toolBar.add(findNearestRouteBtn);
        toolBar.add(searchByRouteBtn);
        toolBar.add(searchBusETABtn);
        toolBar.add(occupancyBtn);

        // Put Alerts tab last, after Occupancy
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

    public void saveConfig() {
        try {
            PrintWriter writer = new PrintWriter("themeConfig.txt", "UTF-8");
            writer.println(currentThemeIndex);
            writer.close();
        }
        catch (IOException e) {
            System.err.println("Error with output file.");
        }
    }
}
