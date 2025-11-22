package app;

import entities.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame {

        public static void main(String[] args) {
                List<Route> routes = new ArrayList<Route>();
                Route route929 = new Route(929);
                route929.addBusStop(new BusStop(1526, 16, "Victoria Park Ave at Navaho Dr",
                                new Position(43.800546, -79.334889)));

                route929.addTrip(new Trip(57459020, route929, new Bus(3640,
                                new Position(43.722424, -79.41525, 254, 0),
                                "UNKNOWN"))); // Initial occupancy unknown, will be fetched

                route929.addTrip(new Trip(11854020, route929, new Bus(3630,
                                new Position(43.723186, -79.49693, 74, 0),
                                "UNKNOWN")));

                route929.addTrip(new Trip(36126020, route929, new Bus(3632,
                                new Position(43.70988, -79.474144, 253, 0),
                                "UNKNOWN")));

                routes.add(route929);

                SwingUtilities.invokeLater(() -> {
                        AppBuilder appBuilder = new AppBuilder();
                        JFrame app = appBuilder
                                        .addMapView()
                                        .addMapUseCase()
                                        .addOccupancyView()
                                        .addOccupancyUseCase()
                                        .build();

                        // Trigger occupancy check for the route
                        appBuilder.getOccupancyController().execute(route929);

                        app.setMinimumSize(new java.awt.Dimension(400, 300));
                        app.pack();
                        app.setLocationRelativeTo(null);
                        app.setVisible(true);
                });

        }
}
