package interface_adapter.map;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import use_case.map.BusListOutput;
import use_case.map.MapOutputBoundary;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MapPresenter implements MapOutputBoundary {
    private final MapViewModel mapViewModel;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public MapPresenter(MapViewModel mapViewModel) {
        this.mapViewModel = mapViewModel;
        // mapViewModel.setWaypoints();
    }

    public void prepareBusView(BusListOutput busList) {
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        for (GeoPosition pos : busList) {
            waypoints.add(new DefaultWaypoint(pos));
        }
        fireWaypointsChanged(new HashSet<Waypoint>(waypoints));
    }

    public void addWaypointChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void fireWaypointsChanged(Set<Waypoint> newWaypoints) {
        Set<Waypoint> oldWaypoints = this.mapViewModel.getWaypoints();
        mapViewModel.setWaypoints(newWaypoints);
        this.support.firePropertyChange("waypoints updated", oldWaypoints, newWaypoints);
    }
}