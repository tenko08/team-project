package interface_adapter.map;

import interface_adapter.ViewModel;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapViewModel extends ViewModel<MapState> {
    public TileFactoryInfo info;
    public static final double LAT = 43.65;
    public static final double LON = -79.38;
    public GeoPosition toronto = new GeoPosition(LAT, LON);
    private Set<Waypoint> waypoints = null;

    public MapViewModel() {
        super("map");

        // Create a TileFactoryInfo for OpenStreetMap
        info = new OSMTileFactoryInfo("OpenStreetMap", "https://tile.openstreetmap.org");

        setState(new MapState());
    }

    public Set<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Set<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }
}
