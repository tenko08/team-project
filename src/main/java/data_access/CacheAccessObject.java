package data_access;

import use_case.map.MapDataAccessInterface;

import java.io.File;

public class CacheAccessObject implements MapDataAccessInterface {
    private File cacheDir;

    public CacheAccessObject() {
        this.cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");;
    }

    public File getCacheDir() {
        return cacheDir;
    }
}
