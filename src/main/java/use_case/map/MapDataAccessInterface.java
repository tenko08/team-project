package use_case.map;

import entities.Bus;

import java.io.File;
import java.util.List;

/**
 * DAO interface for the Map Use Case.
 */
public interface MapDataAccessInterface {
    File getCacheDir();

    List<Bus> getAllBuses();
}
