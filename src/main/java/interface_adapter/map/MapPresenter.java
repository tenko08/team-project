package interface_adapter.map;

import entities.BusIcon;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import use_case.map.MapOutputData;
import use_case.map.MapOutputBoundary;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapPresenter implements MapOutputBoundary {
    private final MapViewModel mapViewModel;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public MapPresenter(MapViewModel mapViewModel) {
        this.mapViewModel = mapViewModel;
        // mapViewModel.setWaypoints();
    }

    public void prepareRouteView(MapOutputData mapOutputData) {
        ArrayList<Waypoint> busLocations = new ArrayList<>();
        ArrayList<Waypoint> routeShapePoints = new ArrayList<>();
        List<GeoPosition> b = mapOutputData.getBuses();
        for (GeoPosition pos : b) {
            busLocations.add(new BusIcon(pos));
        }
        fireWaypointsChanged(new HashSet<Waypoint>(busLocations), mapOutputData.getRoutes());
    }

    public void addWaypointChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void fireWaypointsChanged(Set<Waypoint> newBusLocations, List<GeoPosition> newRouteShapePoints) {
        Set<Waypoint> oldWaypoints = this.mapViewModel.getBusLocations();
        mapViewModel.setBusLocations(newBusLocations);
        mapViewModel.setRouteShapePoints(newRouteShapePoints);
        this.support.firePropertyChange("waypoints updated", null, null);
    }
}