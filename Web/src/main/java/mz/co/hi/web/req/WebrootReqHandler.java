package mz.co.hi.web.req;

import mz.co.hi.web.RequestContext;
import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.Tunnings;
import org.apache.tika.Tika;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mario Junior.
 */
@HandleRequests(regexp = "(webroot[A-Za-z0-9_\\/.-]+[.][A-Za-z]{1,10}|[A-Za-z_\\/-]+\\/webroot[A-Za-z0-9_\\/.-]+[.][A-Za-z]{1,10})\\w+")
@ApplicationScoped
public class WebrootReqHandler extends ReqHandler {

    private RequestContext requestContext = null;
    private HashMap<String,Long> staticCachingDefinitions = new HashMap<>();
    private HashMap<String,Boolean> smartCachedAssets = new HashMap<>();
    private HashMap<String,Long> cachingStatus = new HashMap<>();

    @Inject
    private mz.co.hi.web.AppContext appContext;

    public WebrootReqHandler(){

        List<Tunnings.CachedWebrootDirectory> list = AppConfigurations.get().getTunnings().getCachedWebrootDirectoryList();
        List<String> allSmartCached = AppConfigurations.get().getTunnings().getSmartCachedAssets();

        if(list.size()==0)
            return;

        for(Tunnings.CachedWebrootDirectory cachedWebrootDirectory: list)
            staticCachingDefinitions.put(cachedWebrootDirectory.name,cachedWebrootDirectory.time);

        for(String smartCached : allSmartCached)
            smartCachedAssets.put(smartCached,true);



    }

    private long getCachePeriod(String url){

        long value = -1;

        synchronized (cachingStatus){

            if(cachingStatus.containsKey(url))
                value = cachingStatus.get(url);

        }

        return value;

    }

    private void setCachePeriod(String url, long period){

        synchronized (cachingStatus){

            cachingStatus.put(url,period);

        }

    }

    private long getCacheFirstTime(String url){

        String folderName = getFolderName(url);

        if(folderName==null) {

            setCachePeriod(url, 0);
            return 0;

        }

        long period = 0;

        if(staticCachingDefinitions.containsKey(folderName))
            period = staticCachingDefinitions.get(folderName);

        setCachePeriod(url,period);
        return period;

    }

    private String getFolderName(String url){

        if(url==null)
            throw new NullPointerException("Asset URL is null");

        CharSequence webrootPath = "webroot/";
        url = url.replace(webrootPath,"");

        if(url.length()<3)
            return null;

        int slashIndex = url.indexOf('/');
        if(slashIndex==-1)
            return null;

        String folderName = url.substring(0,slashIndex);
        return folderName;

    }

    private void avoidCaching(RequestContext requestContext){

        requestContext.getResponse()
                .setHeader("Cache-Control", "no-cache");

    }

    private void decideCaching(String assetUrl){

        if(AppConfigurations.get().getDeploymentMode()== AppConfigurations.DeploymentMode.DEVELOPMENT) {
            avoidCaching(requestContext);
            return;
        }

        Tunnings tunnings = AppConfigurations.get().getTunnings();
        if(tunnings.isASmartCachedURL(assetUrl)) {
            assetUrl = tunnings.getCleanAssetURL(assetUrl);
            String assetCacheablePath = assetUrl.substring(assetUrl.indexOf('/')+1,assetUrl.length());
            if(smartCachedAssets.containsKey(assetCacheablePath)){
                tunnings.emmitSmartCachingHeaders(requestContext);
                return;
            }
        }

        long period = getCachePeriod(assetUrl);
        //First time checking if file is cached
        if (period == -1)
            period = getCacheFirstTime(assetUrl);

        //Cache the file
        if (period > 0) {
            requestContext.getResponse().setHeader("Pragma", "");
            requestContext.getResponse().setHeader("Cache-Control", "public, max-age=" + period);
        }else avoidCaching(requestContext);

    }

    private void writeAssetOut(URL assetURL,String mime,OutputStream outputStream) throws  IOException{

        InputStream fileStream = assetURL.openStream();
        outputStream = requestContext.getOutputStream();
        requestContext.getResponse().setHeader("Content-Type", mime);

        int fileSize = fileStream.available();
        requestContext.getResponse().setHeader("Content-Length", String.valueOf(fileSize));

        while (fileStream.available() > 0) {

            byte[] buffer = new byte[4048];

            if (fileStream.available() < 4048)
                buffer = new byte[fileStream.available()];

            int totalRead = fileStream.read(buffer);
            if (totalRead != buffer.length)
                buffer = Arrays.copyOf(buffer, totalRead);

            outputStream.write(buffer);

        }

    }


    public boolean handle(RequestContext requestContext) throws ServletException, IOException {

        this.requestContext = requestContext;
        String assetUrl = requestContext.getRequest().getRequestURI().replace(requestContext.getRequest().getContextPath()+"/","");
        int indexAssetsSlash = assetUrl.lastIndexOf("webroot/");
        assetUrl = assetUrl.substring(indexAssetsSlash,assetUrl.length());

        try {

            decideCaching(assetUrl);
            URL assetURL = requestContext.getServletContext().getResource("/"+assetUrl);

            if(assetURL==null){
                //TODO: Write a warning log
                return false;
            }

            String mime = new Tika().detect(assetURL);
            if(mime==null)
                return false;

            writeAssetOut(assetURL,mime,requestContext.getOutputStream());

        }catch (Throwable ex){

            requestContext.getResponse().sendError(500);

        }

        return true;

    }



}
