package com.web.yayee.mvc;

import com.web.yayee.RequestContext;
import com.web.yayee.Helper;
import com.web.yayee.config.AppConfigurations;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class Controller implements Serializable {

    private FrontEnd frontEnd = null;
    public static final String VIEW_DATA_KEY ="dataJson";
    private RequestContext requestContext;

    @Inject
    private HTMLizer htmLizer;

    public Controller(){

        this.frontEnd = new FrontEnd();

    }


    public JsonObjectBuilder json(){

        return Json.createObjectBuilder();

    }

    public void setRequestContext(RequestContext requestContext){

        this.requestContext = requestContext;

    }

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public void redirect(String url){



    }

    public FrontEnd getFrontEnd(){

        return frontEnd;

    }

    public void callView() throws MvcException{

        this.callView(null);

    }


    public void callView(Map values) throws NoSuchViewException, TemplateException, ConversionFailedException {


        AppConfigurations config = AppConfigurations.get();

        String actionName = requestContext.getData().get("action").toString();
        String controllerName = requestContext.getData().get("controller").toString();
        String viewFile = "/"+config.getViewsDirectory()+"/"+controllerName+"/"+actionName.toString()+".html";
        String viewJsfile = "/"+config.getViewsDirectory()+"/"+controllerName+"/"+actionName.toString()+".js";


        if(values==null){

            values = new HashMap<>();

        }


        Map templateData = this.getFrontEnd().getTemplateData();


        if(templateData.size()>0){

            values.put("$root",templateData);

        }

        requestContext.getData().put(VIEW_DATA_KEY,values);

        //Do not need to load the view file
        if(requestContext.getData().containsKey("ignore_view")){

            htmLizer.setRequestContext(requestContext);
            htmLizer.process(this,true);
            return;

        }


        URL viewResource = null;
        URL viewJsResource = null;

        try {


            viewResource = requestContext.getServletContext().getResource(viewFile);


        }catch (Exception ex){

            throw new NoSuchViewException(controllerName,actionName);

        }



        try{

            viewJsResource   = requestContext.getServletContext().getResource(viewJsfile);

        }catch (Exception ex){



        }

        if(requestContext.getRequest().getHeader("Ignore-Js")==null) {

            if (viewJsResource != null) {


                try {

                    InputStream viewJsInputStream = viewJsResource.openStream();
                    String viewJsContent = requestContext.readToEnd(viewJsInputStream);
                    requestContext.getData().put("view_js", viewJsContent);

                } catch (Exception ex) {

                    ex.printStackTrace();

                }

            }

        }


        if(viewResource==null){

            throw new NoSuchViewException(controllerName,actionName);

        }


        if(requestContext.getRequest().getHeader("Ignore-View")==null){

            try {

                InputStream viewInputStream = viewResource.openStream();
                String viewContent = Helper.readTextStreamToEnd(viewInputStream, requestContext);
                requestContext.getData().put("view_content",viewContent);


            }catch (Exception ex){

                //TODO: Throw exception
                ex.printStackTrace();

            }

        }

        htmLizer.setRequestContext(requestContext);
        htmLizer.process(this,false);

    }

}
