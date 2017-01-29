package mz.co.hi.web;


import com.google.gson.GsonBuilder;
import mz.co.hi.web.boot.BootAgent;
import mz.co.hi.web.config.AppConfigurations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Serializable;

@ApplicationScoped
public class AppContext implements Serializable {

    private static String assetVersionPrefix = ".vd3p1d";

    public static void setAssetVersionPrefix(String prefix){

        assetVersionPrefix = prefix;

    }


    private GsonBuilder gsonBuilder = null;
    private boolean changed = false;

    @Inject
    private BootAgent bootAgent;

    public String getAssetVersionToken(){

       return  assetVersionPrefix+String.valueOf(getDeployId());

    }

    public String getDeployId(){

        return bootAgent.getDeployId();

    }

    public AppConfigurations.DeploymentMode getDeployMode(){

        return AppConfigurations.get().getDeploymentMode();

    }


    @PostConstruct
    public void setup(){

        gsonBuilder = new GsonBuilder();

    }

    public GsonBuilder getGsonBuilder(){

        return gsonBuilder;

    }

    public void setGsonBuilder(GsonBuilder gsonBuilder){

        this.gsonBuilder = gsonBuilder;
        this.changed = true;

    }


}
