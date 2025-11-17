package view;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;

import interface_adapter.map.MapViewModel;
import interface_adapter.map.SelectionAdapter;

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

        // Set focus
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(mapViewModel.toronto);

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);

        mapViewer.addMouseListener(new CenterMapListener(mapViewer));

        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        // Add a selection painter
        SelectionAdapter sa = new SelectionAdapter(mapViewer);
        mapViewer.addMouseListener(sa);
        mapViewer.addMouseMotionListener(sa);

        this.add(mapViewer);
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }

    public String getViewName() { return viewName; }
}
