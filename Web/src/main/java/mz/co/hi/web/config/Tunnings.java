package mz.co.hi.web.config;

import mz.co.hi.web.AppContext;
import mz.co.hi.web.RequestContext;

import javax.enterprise.inject.spi.CDI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mario Junior.
 */
public class Tunnings {

    private List<CachedWebrootDirectory> cachedWebrootDirectoryList = new ArrayList<>();
    private List<String> smartCachedAssets = new ArrayList<>();


    public class CachedWebrootDirectory {

        public String name;
        public long time;

        public CachedWebrootDirectory(String name, long time){

            this.name = name;
            this.time = time;

        }


    }


    public Tunnings(){

        this.smartCachedAssets.add("hi-es5.js");

    }

    public List<CachedWebrootDirectory> getCachedWebrootDirectoryList() {
        return cachedWebrootDirectoryList;
    }

    public void setCachedDirectory(String name, long time){

        cachedWebrootDirectoryList.add(new CachedWebrootDirectory(name,time));

    }

    public void enableSmartCaching(String asset){

        smartCachedAssets.add(asset);

    }

    public List<String> getSmartCachedAssets(){

        return smartCachedAssets;

    }

    public String applySmartCaching(String markup){

        if(AppConfigurations.get().getDeploymentMode()== AppConfigurations.DeploymentMode.DEVELOPMENT)
            return markup;


        AppContext appContext = CDI.current().select(AppContext.class).get();

        if(smartCachedAssets.size()==0)
            return markup;


        String result =  markup;

        for(String sca : smartCachedAssets){

            int lastDotIndex = sca.lastIndexOf('.');
            String newURI = sca.substring(0,lastDotIndex)+appContext.getAssetVersionToken()+sca.substring(lastDotIndex,sca.length());
            result = result.replace(sca,newURI);

        }

        return result;

    }

    public void emmitSmartCachingHeaders(RequestContext requestContext){

        requestContext.getResponse().setHeader("Pragma", "");
        requestContext.getResponse().setHeader("Cache-Control", "public, max-age=31536000");
        //requestContext.getResponse().setHeader("Expires", "Sun, 17-Jan-2038 19:14:07 GMT");

    }

    public boolean isASmartCachedURL(String assetUrl){

        AppContext appContext = CDI.current().select(AppContext.class).get();
        String cachingVersion = appContext.getAssetVersionToken();
        return assetUrl.indexOf(cachingVersion)!=-1;

    }

    public String getCleanAssetURL(String smartCacheURL){

        AppContext appContext = CDI.current().select(AppContext.class).get();
        String cachingVersion = appContext.getAssetVersionToken();
        return smartCacheURL.replace(cachingVersion,"");

    }

}
