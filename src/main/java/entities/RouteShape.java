package entities;

import org.jxmapviewer.viewer.GeoPosition;

import java.util.ArrayList;

public class RouteShape {
    private int id;
    private ArrayList<GeoPosition> points = new ArrayList<>();

    public RouteShape(int id) { this.id = id; }

    public int getId() { return id; }
    public ArrayList<GeoPosition> getPoints() { return points; }
    public void addPoint(GeoPosition pos) { points.add(pos); }
}
