package interface_adapter.map;

import java.awt.event.MouseAdapter;
import use_case.map.MapInputBoundary;

/**
 * Controller for the Map Use Case.
 */
public class MapController extends MouseAdapter {
    private MapInputBoundary mapInteractor;

    public MapController(MapInputBoundary interactor)
    {
        this.mapInteractor = interactor;
    }
}
