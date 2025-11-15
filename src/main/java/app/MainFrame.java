package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

public class MainFrame {
    private static final String TITLE = "Real-Time TTC Map Viewer";
    private static final double LAT = 43.65;
    private static final double LON = -79.38;

    public static void main(String[] args) {
        JXMapViewer mapViewer = new JXMapViewer();

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo("OpenStreetMap", "https://tile.openstreetmap.org");
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setUserAgent("TTC Map Viewer/1.0 (contact: michaeld.kim@mail.utoronto.ca)");
        mapViewer.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        // Set the focus
        GeoPosition toronto = new GeoPosition(LAT, LON);

        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(toronto);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.getContentPane().add(mapViewer);
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
