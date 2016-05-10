package com.yayee.resources;

import java.net.URL;

/**
 * Created by Mario Junior.
 */
public class ResourcesProvider {

    public static URL getYapiys(){

        return ResourcesProvider.class.getResource("yapiys.js");

    }


}
