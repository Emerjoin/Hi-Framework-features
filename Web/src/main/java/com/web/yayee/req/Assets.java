package com.web.yayee.req;

import com.web.yayee.RequestContext;
import com.web.yayee.Helper;
import com.web.yayee.config.AppConfigurations;
import com.web.yayee.config.CachedWebrootDirectory;

import javax.enterprise.context.ApplicationScoped;
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
@HandleRequests(regexp = "(webroot[A-Za-z0-9_\\/.-]+[.][A-Za-z]{1,10}|[A-Za-z_\\/]+\\/webroot[A-Za-z0-9_\\/.-]+[.][A-Za-z]{1,10})\\w+")
@ApplicationScoped
public class Assets extends ReqHandler {


    private RequestContext requestContext = null;
    private HashMap<String,Long> cachingDefs = new HashMap<>();
    private HashMap<String,Long> cachingStatus = new HashMap<>();

    public Assets(){

        List<CachedWebrootDirectory> list = AppConfigurations.get().getTunnings().getCachedWebrootDirectoryList();
        if(list.size()==0)
            return;

        for(CachedWebrootDirectory cachedWebrootDirectory: list)
            cachingDefs.put(cachedWebrootDirectory.name,cachedWebrootDirectory.time);



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

        if(cachingDefs.containsKey(folderName))
            period = cachingDefs.get(folderName);

        setCachePeriod(url,period);
        return period;

    }

    private String getFolderName(String url){

        if(url==null)
            return null;

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

    public boolean handle(RequestContext requestContext) throws ServletException, IOException {

        this.requestContext = requestContext;
        String assetUrl = requestContext.getRequest().getRequestURI().replace(requestContext.getRequest().getContextPath()+"/","");

        int indexAsstesSlash = assetUrl.indexOf("webroot/");
        assetUrl = assetUrl.substring(indexAsstesSlash,assetUrl.length());

        URL assetURL = requestContext.getServletContext().getResource("/"+assetUrl);
        if(assetURL==null){

            //TODO: Do something about
            return false;

        }

        String mime = getMime2(assetUrl);

        if(mime==null){

            return false;

        }


        InputStream fileStream = null;
        OutputStream outputStream = null;

        try {


            long period = getCachePeriod(assetUrl);

            //First time checking if file is cached
            if(period==-1)
                period = getCacheFirstTime(assetUrl);

            //Cache the file
            if(period>0){

                requestContext.getResponse().setHeader("Pragma","");
                requestContext.getResponse().setHeader("Cache-Control","public, max-age="+period);

            }


            fileStream = assetURL.openStream();
            outputStream = requestContext.getOutputStream();
            requestContext.getResponse().setHeader("Content-Type", mime);



            while (fileStream.available() > 0) {

                byte[] buffer = new byte[4048];
                if (fileStream.available() < 4048) {

                    buffer = new byte[fileStream.available()];

                }

                int totalRead = fileStream.read(buffer);
                if (totalRead != buffer.length) {

                    buffer = Arrays.copyOf(buffer, totalRead);

                }

                outputStream.write(buffer);


            }

        }catch (Exception ex){

            requestContext.getResponse().sendError(500);

        }

        return true;

    }


    private static String getMime2(String filename){

        return org.clapper.util.misc.MIMETypeUtil.MIMETypeForFileName(filename);

    }


}
