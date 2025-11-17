package interface_adapter.map;

import interface_adapter.ViewManagerModel;
import use_case.map.MapOutputBoundary;

public class MapPresenter implements MapOutputBoundary {
    private final MapViewModel mapViewModel;

    public MapPresenter(MapViewModel mapViewModel) {
        this.mapViewModel = mapViewModel;
    }
}