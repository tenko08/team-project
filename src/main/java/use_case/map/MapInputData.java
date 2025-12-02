package use_case.map;

import java.awt.geom.Point2D;

public class MapInputData {
    private Point2D point;

    public MapInputData(Point2D point) { this.point = point; }

    public Point2D getPoint() { return point; }
}
