package data_access;

import entities.Bus;
import use_case.map.MapDataAccessInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CacheAccessObject implements MapDataAccessInterface {
    private File cacheDir;

    public CacheAccessObject() {
        this.cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");;
    }

    @Override
    public File getCacheDir() {
        return cacheDir;
    }

    @Override
    public List<Bus> getAllBuses() {
        // CacheAccessObject doesn't handle bus data, return empty list
        // or throw UnsupportedOperationException if this should never be called
        return new ArrayList<>();
    }

}
