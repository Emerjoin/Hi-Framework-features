package com.web.yayee;

/**
 * Created by Mario Junior.
 */
public abstract class FilesCache extends Cache {

    public  abstract boolean isFileCached(String name);
    public abstract boolean cacheFile(String name);


    public static FilesCache getActiveCache(){

        
        return null;

    }


}
