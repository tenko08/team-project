package use_case.occupancy;

import entities.Bus;

public interface OccupancyDataAccessInterface {

    /**
     * Gets the occupancy of the bus
     * @param bus the bus to find the occupancy of
     * @return either Full, Almost Full, or Empty
     */
    String getOccupancy(Bus bus);
    
}
