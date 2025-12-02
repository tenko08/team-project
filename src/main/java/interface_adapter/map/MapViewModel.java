package interface_adapter.map;

import interface_adapter.ViewModel;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;

import java.util.List;
import java.util.Set;

public class MapViewModel extends ViewModel<MapState> {
    public TileFactoryInfo info;
    public static final double LAT = 43.65;
    public static final double LON = -79.38;
    public GeoPosition toronto = new GeoPosition(LAT, LON);
    private Set<Waypoint> busLocations = null;
    private List<GeoPosition> routeShapePoints = null;

    public MapViewModel() {
        super("map");

        // Create a TileFactoryInfo for OpenStreetMap
        info = new OSMTileFactoryInfo("OpenStreetMap", "https://tile.openstreetmap.org");

        setState(new MapState());
    }

    public Set<Waypoint> getBusLocations() { return busLocations; }

    public void setBusLocations(Set<Waypoint> busLocations) {
        this.busLocations = busLocations;
    }

    public List<GeoPosition> getRouteShapePoints() { return routeShapePoints; }

    public void setRouteShapePoints(List<GeoPosition> routeShapePoints) {
        this.routeShapePoints = routeShapePoints;
    }
}
