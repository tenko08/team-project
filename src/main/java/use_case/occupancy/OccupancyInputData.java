package use_case.occupancy;

import entities.Bus;

/**
 * The Input Data for the Occupancy use case.
 */
public class OccupancyInputData {
    private final Bus bus;

    public OccupancyInputData(Bus bus) {
        this.bus = bus;
    }

    Bus getBus() {
        return bus;
    }
}
