package app;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import entities.Bus;
import entities.BusStop;
import entities.Position;
import entities.Route;
import entities.Trip;


public class MainFrame {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppBuilder appBuilder = new AppBuilder();
            JFrame app = appBuilder
                    .addMapView()
                    .addMapUseCase()
                    .addFindNearestRouteView()
                    .addFindNearestRouteUseCase()
                    .addAlertsView()
                    .addSearchByRouteUseCase()
                    .addSearchByRouteView()
                    .addBusScheduleUseCase()
                    .addBusScheduleView()
                    .addOccupancyUseCase()
                    .addOccupancyView()
                    .build();
            app.setMinimumSize(new java.awt.Dimension(300, 200));
            app.pack();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
        });
        List<Route> routes = new ArrayList<Route>();
        Route route929 = new Route(929);
        route929.addBusStop(new BusStop(1526,16,"Victoria Park Ave at Navaho Dr",
                new Position(43.800546,-79.334889)));

//        route929.addTrip(new Trip(72598070, route929, new Bus(9446,
//                new Position(43.65386, -79.43306, 164, 0),
//                "FEW_SEATS_AVAILABLE")));
//
//        route929.addTrip(new Trip(76422070, route929, new Bus(9432,
//                new Position(43.7322, -79.45838, 253, 0),
//                "EMPTY")));

        routes.add(route929);

        }
}
