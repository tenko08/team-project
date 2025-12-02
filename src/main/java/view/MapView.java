package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


import javax.swing.*;
import javax.swing.event.MouseInputListener;

import interface_adapter.map.MapController;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import interface_adapter.map.MapViewModel;
import org.jxmapviewer.viewer.WaypointPainter;

public class MapView extends JPanel implements PropertyChangeListener {
    private final String viewName = "map";
    private final MapViewModel mapViewModel;
    private MapController mapController = null;
    private JXMapViewer mapViewer;
    private TileFactoryInfo info;
    private DefaultTileFactory tileFactory;
    private WaypointPainter busIconPainter = new WaypointPainter();

    public MapView(MapViewModel mapViewModel) {

        this.mapViewModel = mapViewModel;
        setLayout(new BorderLayout());
        mapViewer = new JXMapViewer();

        info = mapViewModel.info;
        tileFactory = new DefaultTileFactory(info);
        tileFactory.setUserAgent("TTC Map Viewer/1.0 (contact: michaeld.kim@mail.utoronto.ca)");
        mapViewer.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        // Set focus
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(mapViewModel.toronto);
        System.out.println(mapViewer.getAddressLocation().getClass());

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);

        mapViewer.addMouseListener(new CenterMapListener(mapViewer));

        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        busIconPainter.setRenderer(new FancyWaypointRenderer());
        mapViewer.setOverlayPainter(busIconPainter);

        // Ensure the map view fills available space
        add(mapViewer, BorderLayout.CENTER);
        // Provide a reasonable preferred size so pack() creates a large map
        setPreferredSize(new Dimension(900, 600));
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }

    public String getViewName() { return viewName; }

    public void setMapController(MapController controller) {
        this.mapController = controller;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        busIconPainter.setWaypoints(mapViewModel.getBusLocations());
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(busIconPainter);
        List<List<GeoPosition>> shapes = mapViewModel.getRouteShapePoints();
        for (List shape : shapes) {
            painters.add(new RoutePainter(shape));
        }
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        mapViewer.setOverlayPainter(painter);
    }
}
