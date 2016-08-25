package mz.co.hi.web;


import com.google.gson.GsonBuilder;
import mz.co.hi.web.config.AppConfigurations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import java.io.Serializable;

@ApplicationScoped
public class AppContext implements Serializable {

    private GsonBuilder gsonBuilder = null;
    private boolean changed = false;

    private static String assetVersionPrefix = ".vd3p1d";

    public static void setAssetVersionPrefix(String prefix){

        assetVersionPrefix = prefix;

    }

    public String getAssetVersionToken(){

       return  assetVersionPrefix+String.valueOf(getDeployId());

    }

    public String getDeployId(){

        return DispatcherServlet.DEPLOY_ID;

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
