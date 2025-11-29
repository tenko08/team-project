package interface_adapter.occupancy;

import use_case.occupancy.OccupancyOutputBoundary;
import use_case.occupancy.OccupancyOutputData;

/**
 * Presenter for the Occupancy Use Case.
 * Updates the ViewModel based on the output from the interactor.
 */
public class OccupancyPresenter implements OccupancyOutputBoundary {
    private final OccupancyViewModel occupancyViewModel;

    public OccupancyPresenter(OccupancyViewModel occupancyViewModel) {
        this.occupancyViewModel = occupancyViewModel;
    }

    @Override
    public void prepareSuccessView(OccupancyOutputData outputData) {
        OccupancyState state = occupancyViewModel.getState();
        if (outputData.getBusId() != -1) {
            state.setOccupancy(outputData.getOccupancyLevel());
            state.getBusOccupancies().put(outputData.getBusId(), outputData.getOccupancyLevel());
        } else {
            state.setBusOccupancies(outputData.getBusOccupancies());
        }
        state.setCurrentRoute(outputData.getRoute());
        state.setError(null);
        occupancyViewModel.setState(state);
        occupancyViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        OccupancyState state = occupancyViewModel.getState();
        state.setError(error);
        occupancyViewModel.setState(state);
        occupancyViewModel.firePropertyChange();
    }

    /**
     * Sets the current route in the state. This should be called when starting to load occupancy for a route.
     */
    public void setCurrentRoute(entities.Route route) {
        OccupancyState state = occupancyViewModel.getState();
        state.setCurrentRoute(route);
        state.getBusOccupancies().clear();
        state.setError(null);
        occupancyViewModel.setState(state);
        occupancyViewModel.firePropertyChange();
    }
}
