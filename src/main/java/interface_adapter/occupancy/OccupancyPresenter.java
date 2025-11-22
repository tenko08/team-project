package interface_adapter.occupancy;

import use_case.occupancy.OccupancyOutputBoundary;
import use_case.occupancy.OccupancyOutputData;

public class OccupancyPresenter implements OccupancyOutputBoundary {
    private final OccupancyViewModel occupancyViewModel;

    public OccupancyPresenter(OccupancyViewModel occupancyViewModel) {
        this.occupancyViewModel = occupancyViewModel;
    }

    @Override
    public void prepareSuccessView(OccupancyOutputData outputData) {
        OccupancyState state = occupancyViewModel.getState();
        state.setOccupancy(outputData.getOccupancyLevel());
        state.getBusOccupancies().put(outputData.getBusId(), outputData.getOccupancyLevel());
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
}
