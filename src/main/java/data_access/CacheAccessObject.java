package data_access;

import java.io.File;

import use_case.map.MapDataAccessInterface;

public class CacheAccessObject implements MapDataAccessInterface {
    private File cacheDir;

    public CacheAccessObject() {
        this.cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");;
    }

    public File getCacheDir() {
        return cacheDir;
    }
}
