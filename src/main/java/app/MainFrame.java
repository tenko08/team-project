package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainFrame {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppBuilder appBuilder = new AppBuilder();
            JFrame app = appBuilder
                    .addMapView()
                    .build();
            app.setMinimumSize(new java.awt.Dimension(300, 200));
            app.pack();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
        });
        
//        Route r = new Route(929);
//        r.addBusStop(new BusStop(1526,16,"Victoria Park Ave at Navaho Dr",
//                new Position(43.800546,-79.334889)));
//
//        r.addTrip(new Trip(72598070, r, new Bus(9446,
//                new Position(43.65386, -79.43306, 164, 0),
//                "FEW_SEATS_AVAILABLE")));
//
//        r.addTrip(new Trip(76422070, r, new Bus(9432,
//                new Position(43.7322, -79.45838, 253, 0),
//                "EMPTY")));


    }
}
