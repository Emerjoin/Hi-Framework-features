package mz.co.hi.web.mvc;

import mz.co.hi.web.DispatcherServlet;
import mz.co.hi.web.FrontEnd;
import mz.co.hi.web.RequestContext;
import mz.co.hi.web.Helper;
import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.mvc.exceptions.ConversionFailedException;
import mz.co.hi.web.mvc.exceptions.MvcException;
import mz.co.hi.web.mvc.exceptions.NoSuchViewException;
import mz.co.hi.web.mvc.exceptions.TemplateException;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class Controller {

    public static final String VIEW_DATA_KEY ="dataJson";

    @Inject
    private HTMLizer htmLizer;


    public Controller(){



    }


    public void redirect(String url){



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
        String viewJsfile = "/"+config.getViewsDirectory()+"/"+controllerName+"/"+actionName.toString()+".js";
        String viewJsMinifiedfile = "/"+config.getViewsDirectory()+"/"+controllerName+"/"+actionName.toString()+".min.js";

        FrontEnd frontEnd = CDI.current().select(FrontEnd.class).get();

        if(!requestContext.hasAjaxHeader()){

            if(DispatcherServlet.templateLoadListener!=null)
                DispatcherServlet.templateLoadListener.onTemplateLoad();


        }

        if(values==null){

            values = new HashMap<>();

        }


        if(frontEnd.wasTemplateDataSet()){

            values.put("$root",frontEnd.getTemplateData());

        }

        requestContext.getData().put(VIEW_DATA_KEY,values);

        //Do not need to load the view file
        if(requestContext.getData().containsKey("ignore_view")){

            htmLizer.process(this,true);
            return;

        }


        URL viewResource = null;
        URL viewJsResource = null;

        try {


            viewResource = requestContext.getServletContext().getResource(viewFile);


        }catch (Exception ex){

            throw new NoSuchViewException(controllerName,actionName,viewFile);

        }



        try{



            if(AppConfigurations.get().underDevelopment())

                viewJsResource   = requestContext.getServletContext().getResource(viewJsfile);

            else{

                //Try the minfied file
                viewJsResource = requestContext.getServletContext().getResource(viewJsMinifiedfile);

                if(viewJsResource==null){

                    viewJsResource   = requestContext.getServletContext().getResource(viewJsfile);

                }


            }



        }catch (Exception ex){

            //TODO: Do something about it
            ex.printStackTrace();

        }

        if(requestContext.getRequest().getHeader("Ignore-Js")==null) {

            if (viewJsResource != null) {


                try {

                    InputStream viewJsInputStream = viewJsResource.openStream();
                    String viewJsContent = Helper.readTextStreamToEnd(viewJsInputStream,null);
                    requestContext.getData().put("view_js", viewJsContent);

                } catch (Exception ex) {

                    //TODO: Do something about it
                    ex.printStackTrace();

                }

            }

        }


        if(viewJsResource==null){

            throw new NoSuchViewException(controllerName,actionName,viewJsfile);

        }


        if(requestContext.getRequest().getHeader("Ignore-View")==null){

            try {

                InputStream viewInputStream = viewResource.openStream();
                String viewContent = Helper.readTextStreamToEnd(viewInputStream, null);
                requestContext.getData().put("view_content",viewContent);


            }catch (Exception ex){

                //TODO: Do something about it
                ex.printStackTrace();

            }

        }

        htmLizer.setRequestContext(requestContext);
        htmLizer.process(this,false);

    }

}
