/*
 * WaypointRenderer.java
 *
 * Created on March 30, 2006, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import entities.BusIcon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

/**
 * A fancy waypoint painter
 * @author Martin Steiger
 */
public class FancyWaypointRenderer implements WaypointRenderer<BusIcon>
{
    private static final Log log = LogFactory.getLog(FancyWaypointRenderer.class);

    private final Map<Color, BufferedImage> map = new HashMap<Color, BufferedImage>();

//    private final Font font = new Font("Lucida Sans", Font.BOLD, 10);

    private BufferedImage origImage;

    /**
     * Uses a default waypoint image
     */
    public FancyWaypointRenderer()
    {
        URL resource = getClass().getResource("/bus.png");

        try
        {
            origImage = ImageIO.read(resource);
        }
        catch (Exception ex)
        {
            log.warn("couldn't read bus.png", ex);
        }
    }

    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer viewer, BusIcon w)
    {
        g = (Graphics2D)g.create();

        if (origImage == null)
            return;

        Point2D point = viewer.getTileFactory().geoToPixel(w.getPosition(), viewer.getZoom());

        int x = (int)point.getX();
        int y = (int)point.getY();

        g.drawImage(origImage, x -origImage.getWidth() / 2, y -origImage.getHeight(), null);

        String label = w.getLabel();

//        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics();
        int tw = metrics.stringWidth(label);
        int th = 1 + metrics.getAscent();

//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(label, x - tw / 2, y + th - origImage.getHeight());

        g.dispose();
    }
}
