package data_access;

import java.io.File;

public class CacheAccessObject {
    private File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");

    public File getCacheDir() {
        return cacheDir;
    }
}
