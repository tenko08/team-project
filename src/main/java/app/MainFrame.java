//package app;
//
//import entities.*;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class MainFrame {
//

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            AppBuilder appBuilder = new AppBuilder();
//            JFrame app = appBuilder
//                    .addMapView()
//                    .addMapUseCase()
//                    .addAlertsUseCase()
//                    .addAlertsView()
//                    .build();
//            app.setMinimumSize(new java.awt.Dimension(300, 200));
//            app.pack();
//            app.setLocationRelativeTo(null);
//            app.setVisible(true);
//        });
//        List<Route> routes = new ArrayList<Route>();
//        Route route929 = new Route(929);
//        route929.addBusStop(new BusStop(1526,16,"Victoria Park Ave at Navaho Dr",
//                new Position(43.800546,-79.334889)));
//
//        route929.addTrip(new Trip(72598070, route929, new Bus(9446,
//                new Position(43.65386, -79.43306, 164, 0),
//                "FEW_SEATS_AVAILABLE")));
//
//        route929.addTrip(new Trip(76422070, route929, new Bus(9432,
//                new Position(43.7322, -79.45838, 253, 0),
//                "EMPTY")));
//
//        routes.add(route929);
//
//    }
//}
package app;

import entities.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("TTC Map Viewer with ETA");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.setBackground(Color.WHITE);

            JButton searchETABtn = new JButton("Search ETA");
            searchETABtn.setBackground(new Color(70, 130, 180));
            searchETABtn.setForeground(Color.BLACK);
            searchETABtn.addActionListener(e -> openBusScheduleETAWindow());

            topPanel.add(searchETABtn);
            mainFrame.add(topPanel, BorderLayout.NORTH);

            AppBuilder appBuilder = new AppBuilder();
            JFrame originalApp = appBuilder
                    .addMapView()
                    .addMapUseCase()
                    .addAlertsUseCase()
                    .addAlertsView()
                    .build();

            mainFrame.add(originalApp.getContentPane(), BorderLayout.CENTER);

            mainFrame.setMinimumSize(new Dimension(200, 300));
            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });


        List<Route> routes = new ArrayList<Route>();
        Route route929 = new Route(929);
        route929.addBusStop(new BusStop(1526,16,"Victoria Park Ave at Navaho Dr",
                new Position(43.800546,-79.334889)));

        route929.addTrip(new Trip(72598070, route929, new Bus(9446,
                new Position(43.65386, -79.43306, 164, 0),
                "FEW_SEATS_AVAILABLE")));

        route929.addTrip(new Trip(76422070, route929, new Bus(9432,
                new Position(43.7322, -79.45838, 253, 0),
                "EMPTY")));

        routes.add(route929);
    }

    private static void openBusScheduleETAWindow() {
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrameBusScheduleEta().createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}