package interface_adapter.map;

import interface_adapter.ViewModel;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Set;

public class MapViewModel extends ViewModel<MapState> {
    public TileFactoryInfo info;
    public static final double LAT = 43.65;
    public static final double LON = -79.38;
    public GeoPosition toronto = new GeoPosition(LAT, LON);
    private Set<Waypoint> busLocations = null;
    private Waypoint cursorWaypoint;
    private List<List<GeoPosition>> routeShapePoints = null;
    private GeoPosition clickPosition;

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

    public List<List<GeoPosition>> getRouteShapePoints() { return routeShapePoints; }

    public void setRouteShapePoints(List<List<GeoPosition>> routeShapePoints) {
        this.routeShapePoints = routeShapePoints;
    }

    public Waypoint getCursorWaypoint() { return cursorWaypoint; }
    public void setCursorWaypoint(Waypoint cursorWaypoint) { this.cursorWaypoint = cursorWaypoint; }
    public GeoPosition getClickPosition() { return clickPosition; }
    public void setClickPosition(GeoPosition clickPosition) { this.clickPosition = clickPosition; }
}
