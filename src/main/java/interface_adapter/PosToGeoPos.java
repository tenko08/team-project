package interface_adapter;

import entities.Position;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * We have two similar entities! (Position and GeoPosition)
 * This adapter converts Position objects to GeoPosition objects
 */

public class PosToGeoPos {
    public static GeoPosition toGeoPosition(Position pos) {
        return new GeoPosition(pos.getLatitude(), pos.getLongitude());
    }
}
