package interface_adapter.map;

import interface_adapter.ViewManagerModel;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.Waypoint;
import use_case.map.MapOutputBoundary;
import use_case.map.MapOutputData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MapPresenter implements MapOutputBoundary {
    private final MapViewModel mapViewModel;

    public MapPresenter(MapViewModel mapViewModel) {
        this.mapViewModel = mapViewModel;
    }

    public void prepareBusView(MapOutputData outputData) {
        Set<Waypoint> waypoints = new HashSet<Waypoint>(Arrays.asList(new DefaultWaypoint(mapViewModel.toronto)));
        mapViewModel.setWaypoints(waypoints);
    }
}