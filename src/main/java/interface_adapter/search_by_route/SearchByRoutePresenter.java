package interface_adapter.search_by_route;

import use_case.search_by_route.SearchByRouteOutputBoundary;
import use_case.search_by_route.SearchByRouteOutputData;

public class SearchByRoutePresenter implements SearchByRouteOutputBoundary {
    private final SearchByRouteViewModel viewModel;

    public SearchByRoutePresenter(SearchByRouteViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(SearchByRouteOutputData outputData) {
        viewModel.setSuccess(true);
        viewModel.setRoute(outputData.getRoute());
        viewModel.setBuses(outputData.getBuses());
        viewModel.setCachedData(false);
        viewModel.setErrorMessage(null);
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareCachedView(SearchByRouteOutputData outputData) {
        viewModel.setSuccess(true);
        viewModel.setRoute(outputData.getRoute());
        viewModel.setBuses(outputData.getBuses());
        viewModel.setCachedData(true);
        viewModel.setErrorMessage("Displaying cached data. API call failed. Click retry to fetch fresh data.");
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setSuccess(false);
        viewModel.setRoute(null);
        viewModel.setBuses(null);
        viewModel.setCachedData(false);
        viewModel.setErrorMessage(errorMessage);
        viewModel.firePropertyChanged();
    }
}
