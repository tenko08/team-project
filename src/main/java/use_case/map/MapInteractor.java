package use_case.map;

public class MapInteractor implements MapInputBoundary {
    private final MapDataAccessInterface cacheDataAccessObject;
    private final MapOutputBoundary mapPresenter;

    public MapInteractor(MapDataAccessInterface mapDataAccessInterface, MapOutputBoundary mapOutputBoundary) {
        this.cacheDataAccessObject = mapDataAccessInterface;
        this.mapPresenter = mapOutputBoundary;
    }
}
