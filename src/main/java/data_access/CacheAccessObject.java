package data_access;

import use_case.map.MapDataAccessInterface;

import java.io.File;

import use_case.occupancy.OccupancyDataAccessInterface;
import entities.Bus;

public class CacheAccessObject implements MapDataAccessInterface, OccupancyDataAccessInterface {
    private File cacheDir;

    public CacheAccessObject() {
        this.cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    @Override
    public String getOccupancy(Bus bus) {
        if (bus != null) {
            return bus.getOccupancy();
        }
        return null;
    }
}
