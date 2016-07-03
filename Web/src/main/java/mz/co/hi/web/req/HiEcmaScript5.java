package mz.co.hi.web.req;

import mz.co.hi.web.AppContext;
import mz.co.hi.web.RequestContext;
import mz.co.hi.web.Helper;
import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.DispatcherServlet;
import mz.co.hi.web.mvc.exceptions.NoSuchTemplateException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mario Junior.
 */
@HandleRequests(regexp = "(hi-[A-Za-z_0-9.-]+[.][A-Za-z]{1,10}|[A-Z-a-z_]+\\/hi-[A-Za-z_0-9.-]+[.][A-Za-z]{1,10})\\w+")
@ApplicationScoped
public class HiEcmaScript5 extends ReqHandler {

    private RequestContext requestContext = null;
    private static Map<String,String> templateControllers = new HashMap<String, String>();

    @Inject
    private AppContext appContext;

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

        String ecmascript5File = "hi-es5.js";
        if(AppConfigurations.get().getDeploymentMode()!= AppConfigurations.DeploymentMode.DEVELOPMENT)
            ecmascript5File = "hi-es5"+appContext.getAssetVersionToken()+".js";


        if(requestURL.equals(ecmascript5File)) {

            requestContext.getResponse().setHeader("Content-Type", "text/javascript");

            if(!AppConfigurations.get().underDevelopment())
                AppConfigurations.get().getTunnings().emmitSmartCachingHeaders(requestContext);
            else
                requestContext.getResponse().setHeader("Cache-Control", "no-cache");



            String replaceble = "//{{config}}";
            String hiJs = getHiJs(requestContext);
            String hiJsReplaced = hiJs.replace(replaceble, DispatcherServlet.javascriptConfigScript);
            Helper.echo(hiJsReplaced + templateContent + DispatcherServlet.componentsScript, requestContext);


        }else if(requestURL.equals("hi-angular.js")){

            requestContext.getResponse().setHeader("Content-Type","text/javascript");
            Helper.echo(DispatcherServlet.angularJsScript,requestContext);


        }else if(requestURL.equals("hi-es5-tests.js")){

            requestContext.getResponse().setHeader("Content-Type","text/javascript");

            String replaceble = "//{{config}}";
            String hiJs = DispatcherServlet.hiLibScript;
            String hiForTests = hiJs+DispatcherServlet.hiTestsScript;


            URL runResource = requestContext.getServletContext().getResource("/webroot/tests/run.js");
            if(runResource!=null){

                String runScript = Helper.readTextStreamToEnd(runResource.openStream(),null);
                hiForTests=hiForTests.replace(replaceble,runScript);

            }


            Set<String> testFiles = AppConfigurations.get().getTestedViews().keySet();
            for(String testFile: testFiles){

                URL resource = requestContext.getServletContext().getResource(testFile);
                String url = AppConfigurations.get().getTestedViews().get(testFile);

                int slashIndex = url.indexOf('/');
                String controller = url.substring(0,slashIndex);
                String action = url.substring(slashIndex+1,url.length());

                String append = "";
                String prepend = "";

                if(resource!=null){

                    String setControllerinfo = "\nHi.$nav.setNextControllerInfo(\""+controller+"\",\""+action+"\");";
                    String setLoadedController = "\nHi.$ui.js.setLoadedController(\""+controller+"\",\""+action+"\");";

                    prepend = setControllerinfo;
                    append =  setLoadedController;

                    String viewControllerContent = Helper.readTextStreamToEnd(resource.openStream(),null);
                    hiForTests=hiForTests+prepend+"\n"+viewControllerContent+append;

                }

            }


            Helper.echo(hiForTests,requestContext);


        }else{

            return false;

        }


        return true;


    }

    private static String getHiJs(RequestContext requestContext){

        String content = null;

        if(DispatcherServlet.hiLibScript !=null){

            content = DispatcherServlet.angularJsScript+DispatcherServlet.hiLibScript+DispatcherServlet.frontiersScript;

        }else

            content = "";


        return content;

    }


}
