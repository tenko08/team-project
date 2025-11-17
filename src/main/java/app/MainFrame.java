package app;

import entities.*;

import javax.swing.*;

public class MainFrame {
    private static final String TITLE = "Real-Time TTC Map Viewer";
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setMinimumSize(new java.awt.Dimension(300, 200));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
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
