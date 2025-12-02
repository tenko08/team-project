package entities;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class BusIcon extends DefaultWaypoint {
    private final String label;

    /**
     * @param label the text
     * @param coord the coordinate
     */
    public BusIcon(String label, GeoPosition coord)
    {
        super(coord);
        this.label = label;
    }

    public BusIcon (GeoPosition coord) {
        super(coord);
        this.label = "";
    }

    /**
     * @return the label text
     */
    public String getLabel()
    {
        return label;
    }
}
