package entities;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

public class WaypointFactory {
    public Waypoint create(GeoPosition pos) { return new DefaultWaypoint(pos); }
}
