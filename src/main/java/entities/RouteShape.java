package entities;

import org.jxmapviewer.viewer.GeoPosition;

import java.util.ArrayList;

public class RouteShape {
    private int id;
    private String branch;
    private ArrayList<GeoPosition> points = new ArrayList<>();

    public RouteShape(int id, String branch) {
        this.id = id;
        this.branch = branch;
    }

    public int getId() { return id; }
    public String getBranch() { return branch; }
    public ArrayList<GeoPosition> getPoints() { return points; }
    public void addPoint(GeoPosition pos) { points.add(pos); }
}
