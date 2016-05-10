package com.web.yayee.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mario Junior.
 */
public class Tunnings {

    private List<CachedWebrootDirectory> cachedWebrootDirectoryList = new ArrayList<>();

    public Tunnings(){



    }

    public List<CachedWebrootDirectory> getCachedWebrootDirectoryList() {
        return cachedWebrootDirectoryList;
    }

    public void setCachedWebrootDirectoryList(List<CachedWebrootDirectory> cachedWebrootDirectoryList) {
        this.cachedWebrootDirectoryList = cachedWebrootDirectoryList;
    }
}
