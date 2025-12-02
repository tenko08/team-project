package use_case.map;

import org.jxmapviewer.viewer.GeoPosition;

import java.util.List;

public class MapOutputData{
    private List<GeoPosition> buses;
    private List<List<GeoPosition>> routeShapePoints;
    public MapOutputData(List<GeoPosition> buses, List<List<GeoPosition>> routeShapePoints) {
        this.buses = buses;
        this.routeShapePoints = routeShapePoints;
    }
    public List<GeoPosition> getBuses() { return this.buses; }
    public List<List<GeoPosition>> getRouteShapePoints() { return this.routeShapePoints; }
}
