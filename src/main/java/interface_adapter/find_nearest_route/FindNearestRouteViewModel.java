package interface_adapter.find_nearest_route;

import interface_adapter.ViewModel;

public class FindNearestRouteViewModel extends ViewModel<FindNearestRouteState> {
    public FindNearestRouteViewModel() {
        super("FindNearestRouteView");
        setState(new FindNearestRouteState());
    }
}
