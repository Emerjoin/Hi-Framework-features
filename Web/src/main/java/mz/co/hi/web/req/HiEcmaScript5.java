package mz.co.hi.web.req;

import mz.co.hi.web.RequestContext;
import mz.co.hi.web.Helper;
import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.DispatcherServlet;
import mz.co.hi.web.mvc.NoSuchTemplateException;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
@HandleRequests(regexp = "(hi-[A-Za-z_0-9.]+[.][A-Za-z]{1,10}|[A-Z-a-z_]+\\/hi-[A-Za-z_0-9.]+[.][A-Za-z]{1,10})\\w+")
@ApplicationScoped
public class HiEcmaScript5 extends ReqHandler {

    private RequestContext requestContext = null;
    private static Map<String,String> templateControllers = new HashMap<String, String>();


    public static void prepareTemplates(ServletContext context) throws ServletException{

        AppConfigurations appConfigurations = AppConfigurations.get();

        for(String template : appConfigurations.getTemplates()) {

            try {


                URL templateHTML = context.getResource("/" + template + ".html");
                URL templateController = context.getResource("/" + template + ".js");

                if(!(templateHTML!=null&&templateController!=null)){

                    throw new NoSuchTemplateException(template);

                }



                String templHTML = Helper.readTextStreamToEnd(templateHTML.openStream(),null);
                MVC.storeTemplate(template,templHTML);



                String templtController = Helper.readTextStreamToEnd(templateController.openStream(), null);
                templateControllers.put(template, templtController);

            }catch (MalformedURLException e){

                throw new ServletException("Invalid template path <"+template+">");


            }catch (IOException ex){

                throw new ServletException("Template <"+template+"> HTML file or Controller could not be found. Make sure they both exist");

            }


        }

    }

    @Override
    public boolean handle(RequestContext requestContext) throws ServletException, IOException {

        this.requestContext = requestContext;

        String requestURL = requestContext.getRequest().getRequestURI().replace(requestContext.getRequest().getContextPath()+"/","");
        //Helper.echo(requestURL);
        int indexOfLastSlash = requestURL.lastIndexOf('/');

        Map<String,Object> requestData = requestContext.getData();
        String templateName = "index";

        if(requestData.containsKey("template")){

            templateName = requestData.get("template").toString();

        }


        String templateContent = "";

        if(templateControllers.containsKey(templateName)){

            templateContent = templateControllers.get(templateName).toString();

        }

        if(indexOfLastSlash!=-1){

            requestURL= requestURL.substring(indexOfLastSlash+1,requestURL.length());

        }

        if(requestURL.equals("hi-es5.js")){

            requestContext.getResponse().setHeader("Content-Type","text/javascript");
            requestContext.getResponse().setHeader("Pragma","");
            requestContext.getResponse().setHeader("Cache-Control","public, max-age=31536000");
            Helper.echo(DispatcherServlet.javascriptInitScript+getYapiysJs(requestContext)+templateContent, requestContext);

        }
        return true;


    }

    private static String getYapiysJs(RequestContext requestContext){

        String content = null;

        if(DispatcherServlet.yayeeLibScript !=null){

            content = DispatcherServlet.yayeeLibScript;

        }else

            content = "";


        return content;

    }


}
