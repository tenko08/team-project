package view;

import interface_adapter.map.MapViewModel;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;

public class MapView extends JPanel {
    private final String viewName = "map";
    private final MapViewModel mapViewModel;
    private JXMapViewer mapViewer;
    private TileFactoryInfo info;
    private DefaultTileFactory tileFactory;

    public MapView(MapViewModel mapViewModel) {
        this.mapViewModel = mapViewModel;
        mapViewer = new JXMapViewer();

        info = mapViewModel.info;
        tileFactory = new DefaultTileFactory(info);
        tileFactory.setUserAgent("TTC Map Viewer/1.0 (contact: michaeld.kim@mail.utoronto.ca)");
        mapViewer.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(mapViewModel.position);

        this.add(mapViewer);
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }

    public String getViewName() { return viewName; }
}
