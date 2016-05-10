package com.web.yayee.app;


import com.google.gson.GsonBuilder;
import com.sun.xml.ws.developer.Stateful;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.Serializable;

@Singleton
@Stateful
public class AppContext implements Serializable {

    private GsonBuilder gsonBuilder = null;
    private boolean changed = false;


    @PostConstruct
    public void setup(){

        gsonBuilder = new GsonBuilder();

    }

    public GsonBuilder getGsonBuilder(){

        System.out.println("App Context changed : "+this.changed);
        return gsonBuilder;

    }

    public void setGsonBuilder(GsonBuilder gsonBuilder){

        this.gsonBuilder = gsonBuilder;
        this.changed = true;

    }


}
