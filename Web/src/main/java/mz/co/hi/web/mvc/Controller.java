package mz.co.hi.web.mvc;

import mz.co.hi.web.FrontEnd;
import mz.co.hi.web.RequestContext;
import mz.co.hi.web.Helper;
import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.events.TemplateLoadEvent;
import mz.co.hi.web.internal.Logging;
import mz.co.hi.web.mvc.exceptions.ConversionFailedException;
import mz.co.hi.web.mvc.exceptions.MvcException;
import mz.co.hi.web.mvc.exceptions.NoSuchViewException;
import mz.co.hi.web.mvc.exceptions.TemplateException;
import org.slf4j.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
//TODO: JavaDoc
//TODO: Refactor
public class Controller {

    public static final String VIEW_DATA_KEY ="dataJson";

    @Inject
    private HTMLizer htmLizer;

    @Inject
    private Event<TemplateLoadEvent> templateLoadEvent;

    private static Logger _log = Logging.getInstance().getLogger();

    private void prepareView(RequestContext requestContext, String controllerName, String actionName,
                             String viewFile, String viewJsfile, String viewJsMinifiedfile) throws NoSuchViewException{
        URL viewResource = null;
        URL viewJsResource = null;

        try {
            viewResource = requestContext.getServletContext().getResource(viewFile);
            if(viewResource==null)
                throw new NoSuchViewException(controllerName,actionName,viewFile);

        }catch (Throwable ex){
            throw new NoSuchViewException(controllerName,actionName,viewFile);
        }

        try{

            if(AppConfigurations.get().underDevelopment())
                viewJsResource   = requestContext.getServletContext().getResource(viewJsfile);
            else{
                viewJsResource = requestContext.getServletContext().getResource(viewJsMinifiedfile);
                if(viewJsResource==null)
                    viewJsResource   = requestContext.getServletContext().getResource(viewJsfile);
            }

        }catch (Throwable ex){
            _log.error(String.format("Failed to get the View JS Resource using: %s AND %s",viewJsfile,viewJsMinifiedfile),ex);
            return;
        }

        if(requestContext.getRequest().getHeader("Ignore-Js")==null)
            doNotIgnoreJS(requestContext,viewJsResource);

        if(viewJsResource==null)
            throw new NoSuchViewException(controllerName,actionName,viewJsfile);

        if(requestContext.getRequest().getHeader("Ignore-View")==null)
            doNotIgnoreView(requestContext,viewResource);

    }

    private void doNotIgnoreJS(RequestContext requestContext, URL viewJsResource){

        if (viewJsResource != null) {
            try {

                InputStream viewJsInputStream = viewJsResource.openStream();
                String viewJsContent = Helper.readLines(viewJsInputStream,null);
                requestContext.getData().put("view_js", viewJsContent);
            } catch (Exception ex) {

                _log.error(String.format("Failed to read the View JS Resource : %s",viewJsResource.getPath()),ex);

            }
        }

    }

    private void doNotIgnoreView(RequestContext requestContext, URL viewResource){

        try {

            InputStream viewInputStream = viewResource.openStream();
            String viewContent = Helper.readLines(viewInputStream, null);
            requestContext.getData().put("view_content",viewContent);


        }catch (Exception ex){

            _log.error(String.format("Failed to read the View HTML Resource : %s",viewResource.getPath()),ex);

        }

    }

    public void callView() throws MvcException {

        this.callView(null);

    }

    public void callView(Map values) throws NoSuchViewException, TemplateException, ConversionFailedException {
        AppConfigurations config = AppConfigurations.get();
        RequestContext requestContext = CDI.current().select(RequestContext.class).get();

        String actionName = requestContext.getData().get("actionU").toString();
        String controllerName = requestContext.getData().get("controllerU").toString();
        String viewFile = "/"+config.getViewsDirectory()+"/"+controllerName+"/"+actionName.toString()+".html";
        String viewJSFile = "/"+config.getViewsDirectory()+"/"+controllerName+"/"+actionName.toString()+".js";
        String viewJSMiniFile = "/"+config.getViewsDirectory()+"/"+controllerName+"/"+actionName.toString()+".min.js";

        FrontEnd frontEnd = CDI.current().select(FrontEnd.class).get();
        if(!requestContext.hasAjaxHeader())
            templateLoadEvent.fire(new TemplateLoadEvent());

        if(values==null)
            values = new HashMap<>();
        if(frontEnd.wasTemplateDataSet())
            values.put("$root",frontEnd.getTemplateData());

        requestContext.getData().put(VIEW_DATA_KEY,values);

        //Do not need to load the view file
        if(requestContext.getData().containsKey("ignore_view")){
            htmLizer.process(this,true);
            return;
        }

        prepareView(requestContext,controllerName,actionName,viewFile,viewJSFile,viewJSMiniFile);
        htmLizer.setRequestContext(requestContext);
        htmLizer.process(this,false);

    }


}
