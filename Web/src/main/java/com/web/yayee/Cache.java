package com.web.yayee;

/**
 * Created by Mario Junior.
 */
public abstract class Cache {

    public  abstract boolean contains(String key);
    public  abstract void store(long maxtime, String content);

    public static Cache getActiveCache(){

        return null;

    }


}
