package interface_adapter.map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.jxmapviewer.JXMapViewer;
import use_case.map.MapInputBoundary;
import use_case.map.MapInputData;

/**
 * Controller for the Map Use Case.
 */
public class MapController extends MouseAdapter {
    private MapInputBoundary mapInteractor;

    public MapController(MapInputBoundary interactor)
    {
        this.mapInteractor = interactor;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON3)
            return;
        Point2D mousePos = new Point(e.getX(), e.getY());
        mapInteractor.markWaypoint(new MapInputData(mousePos));
    }

    public void setMapViewer(JXMapViewer mapViewer) {
        mapInteractor.setMapViewer(mapViewer);
    }
}
