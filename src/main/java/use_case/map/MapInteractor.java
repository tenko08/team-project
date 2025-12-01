package use_case.map;

import entities.Bus;
import org.jxmapviewer.viewer.GeoPosition;
import interface_adapter.PosToGeoPos;
import use_case.search_by_route.SearchByRouteOutputData;

import java.util.ArrayList;
import java.util.List;

public class MapInteractor implements MapInputBoundary {
    private final MapDataAccessInterface cacheDataAccessObject;
    private final MapDataAccessInterface busDataAccessObject;
    private final MapOutputBoundary mapPresenter;

    public MapInteractor(MapDataAccessInterface cacheDataAccessObject, MapDataAccessInterface busDataAccessObject,
                         MapOutputBoundary mapOutputBoundary) {
        this.cacheDataAccessObject = cacheDataAccessObject;
        this.busDataAccessObject = busDataAccessObject;
        this.mapPresenter = mapOutputBoundary;
    }

    @Override
    public void showBuses(SearchByRouteOutputData searchByRouteOutputData) {
        List<Bus> buses = searchByRouteOutputData.getBuses();
        ArrayList<GeoPosition> geoPositions = new ArrayList<>();
        for (Bus bus: buses) {
            geoPositions.add(PosToGeoPos.toGeoPosition(bus.getPosition()));
        }
        BusListOutput busListOutput = new BusListOutput(geoPositions);
        mapPresenter.prepareBusView(busListOutput);
    }
}
