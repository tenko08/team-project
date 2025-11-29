package interface_adapter.occupancy;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Occupancy View.
 */
public class OccupancyViewModel extends ViewModel<OccupancyState> {

    public OccupancyViewModel() {
        super("occupancy");
        setState(new OccupancyState());
    }
}
