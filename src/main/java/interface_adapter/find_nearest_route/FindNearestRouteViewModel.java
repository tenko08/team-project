package interface_adapter.find_nearest_route;

import interface_adapter.ViewModel;
import org.jxmapviewer.viewer.GeoPosition;

public class FindNearestRouteViewModel extends ViewModel<FindNearestRouteState> {
    private GeoPosition cursorWaypoint;
    public FindNearestRouteViewModel() {
        super("FindNearestRouteView");
        setState(new FindNearestRouteState());
    }

    public GeoPosition getCursorWaypoint() { return cursorWaypoint; }
    public void setCursorWaypoint(GeoPosition cursorWaypoint) {}
}
