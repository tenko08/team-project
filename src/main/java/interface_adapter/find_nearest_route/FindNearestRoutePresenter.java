package interface_adapter.find_nearest_route;

import use_case.find_nearest_route.FindNearestRouteOutputBoundary;
import use_case.find_nearest_route.FindNearestRouteOutputData;

public class FindNearestRoutePresenter implements FindNearestRouteOutputBoundary {

//    private final FindNearestRouteViewModel findNearestRouteViewModel;
//    private final ViewManagerModel viewManagerModel;
//
//    public FindNearestRoutePresenter (ViewManagerModel viewManagerModel) {
//        this.viewManagerModel = viewManagerModel;
//    }

    @Override
    public void prepareSuccessView(FindNearestRouteOutputData outputData) {

    }

    @Override
    public void prepareFailView(String message) {

    }
}
