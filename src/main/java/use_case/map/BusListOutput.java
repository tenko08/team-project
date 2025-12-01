package use_case.map;

import org.jetbrains.annotations.NotNull;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.Iterator;
import java.util.List;

public class BusListOutput implements Iterable<GeoPosition> {
    private List<GeoPosition> buses;
    public BusListOutput(List<GeoPosition> buses) {
        this.buses = buses;
    }
    public List<GeoPosition> getBuses() { return this.buses; }

    @NotNull
    @Override
    public Iterator iterator() {
        return buses.iterator();
    }
}
