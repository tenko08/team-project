package interface_adapter.find_nearest_route;

import use_case.find_nearest_route.FindNearestRouteOutputBoundary;
import use_case.find_nearest_route.FindNearestRouteOutputData;

public class FindNearestRoutePresenter implements FindNearestRouteOutputBoundary {

    private final FindNearestRouteViewModel findNearestRouteViewModel;
//    private final ViewManagerModel viewManagerModel;

    public FindNearestRoutePresenter (
//            ViewManagerModel viewManagerModel
            FindNearestRouteViewModel findNearestRouteViewModel) {
        this.findNearestRouteViewModel = findNearestRouteViewModel;
//        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(FindNearestRouteOutputData outputData) {
        System.out.println(outputData);
        final FindNearestRouteState findNearestRouteState = findNearestRouteViewModel.getState();
        findNearestRouteState.setRoute(outputData.getRoute());
        findNearestRouteState.setBusStop(outputData.getBusStop());
        findNearestRouteState.setDistance(outputData.getDistance());

        this.findNearestRouteViewModel.firePropertyChange();
        this.findNearestRouteViewModel.setState(new FindNearestRouteState());
    }

    @Override
    public void prepareFailView(String message) {
        final FindNearestRouteState findNearestRouteState = findNearestRouteViewModel.getState();
        findNearestRouteState.setSearchError(message);
        this.findNearestRouteViewModel.firePropertyChange();
    }
}
