package use_case.map;

import org.jetbrains.annotations.NotNull;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.Iterator;
import java.util.List;

public class MapOutputData{
    private List<GeoPosition> buses;
    private List<GeoPosition> routeShapePoints;
    public MapOutputData(List<GeoPosition> buses, List<GeoPosition> routeShapePoints) {
        this.buses = buses;
        this.routeShapePoints = routeShapePoints;
    }
    public List<GeoPosition> getBuses() { return this.buses; }
    public List<GeoPosition> getRoutes() { return this.routeShapePoints; }

    public List<GeoPosition> getBusLocations() { return this.buses; }
    public List<GeoPosition> getRouteShapePoints() { return this.routeShapePoints; }
}
