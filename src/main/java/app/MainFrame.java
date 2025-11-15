package app;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import entities.Route;

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
            AppBuilder appBuilder = new AppBuilder();
            JFrame app = appBuilder
                    .addMapView()
                    .build();
            app.setMinimumSize(new java.awt.Dimension(300, 200));
            app.pack();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
        });
        List<Route> routes = new ArrayList<Route>();
        Route route929 = new Route(929);

        routes.add(route929);

    }
}
