package mz.co.hi.web;

import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.ConfigLoader;
import mz.co.hi.web.exceptions.HiException;
import mz.co.hi.web.frontier.Scripter;
import mz.co.hi.web.frontier.model.FrontierClass;
import mz.co.hi.web.frontier.model.BeansCrawler;
import mz.co.hi.web.meta.Frontier;
import mz.co.hi.web.meta.WebComponent;
import mz.co.hi.web.mvc.exceptions.MissingResourcesLibException;
import mz.co.hi.web.req.*;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Created by Mario Junior.
 */
@WebServlet(urlPatterns = "/*",name = "HiServlet",loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    public static String hiTestsScript = "";
    public static String hiLibScript = null;
    public static String hiScript = null;
    public static String genericFrontierScript = null;
    public static String javascriptConfigScript ="";
    public static String frontiersScript ="";
    public static String angularJsScript ="";
    public static String componentsScript="";

    protected static  String DEPLOY_ID="";

    private HashMap<String,String> matchedUrls = new HashMap();

    public DispatcherServlet(){



    }


    private void readLibScript() throws MissingResourcesLibException {

        try {


            System.out.println("Reading main client-side code file...");
            URL res = this.getServletContext().getResource("/hi.js");
            if(res!=null){

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                hiLibScript = scriptContent;

            }


            res = this.getServletContext().getResource("/hi-tests.js");
            if(res!=null){

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                hiTestsScript = scriptContent;

            }

            res = this.getServletContext().getResource("/angular.min.js");
            if(res!=null){

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                angularJsScript = scriptContent;

            }



        }catch (Exception ex){


            throw new MissingResourcesLibException(ex);

        }


    }

    private void readLoaderScript() throws MissingResourcesLibException {

        try {

            System.out.println("Reading client-side AJAX loader code file...");
            URL res = this.getServletContext().getResource("/loader.js");
            if(res!=null){

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                hiScript = scriptContent;


            }


        }catch (Exception ex){

            throw new MissingResourcesLibException(ex);

        }

    }

    private void readGenericFrontier() throws MissingResourcesLibException {


        try {

            System.out.println("Reading generic client-side (frontiers) code file...");
            URL res = this.getServletContext().getResource("/frontier.js");
            if(res!=null){

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                genericFrontierScript = scriptContent;


            }


        }catch (Exception ex){

            throw new MissingResourcesLibException(ex);

        }


    }


    private void readJavascriptInit() throws MissingResourcesLibException {

        try {


            URL res = this.getServletContext().getResource("/run.js");
            if(res!=null){

                System.out.println("Reading javascript configurations code file...");

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                javascriptConfigScript = scriptContent;

            }


        }catch (Exception ex){

            throw new MissingResourcesLibException(ex);

        }


    }

    private void generateFrontiers() throws ServletException {

            List beansList = new ArrayList();

            try {

                Class initializerclass = Class.forName("mz.co.hi.web.generated.FrontiersInitializer");
                Constructor constructor = initializerclass.getDeclaredConstructor(List.class);
                constructor.newInstance(beansList);

            }catch (Exception ex){



            }


            System.out.println("Looking for frontiers...");

            FrontierClass[] beanClasses = BeansCrawler.getInstance().crawl(beansList);

            if(beanClasses==null){

                System.out.println("No frontier found. Check if Hi annotations processor is configured.");
                return;

            }



            System.out.println("Generating client-side code for frontiers...");
            Scripter scripter = new Scripter();

            for(FrontierClass beanClass : beanClasses){

                Frontiers.addFrontier(beanClass);
                String frontier_script = scripter.generate(beanClass);
                frontiersScript +="\n"+frontier_script;

            }


    }


    public void init() throws ServletException{
        super.init();

        ConfigLoader.load(this.getServletContext());

        readLibScript();
        readJavascriptInit();
        readLoaderScript();
        readGenericFrontier();
        generateFrontiers();
        findAndLoadComponents();


        if(AppConfigurations.get()!=null){

            ReqHandler.register(CDI.current().select(MVC.class).get(),MVC.class);
            ReqHandler.register(CDI.current().select(Assets.class).get(),Assets.class);
            ReqHandler.register(CDI.current().select(HiEcmaScript5.class).get(),HiEcmaScript5.class);
            ReqHandler.register(CDI.current().select(Frontiers.class).get(),Frontiers.class);
            ReqHandler.register(CDI.current().select(Tests.class).get(),Tests.class);
            ReqHandler.register(CDI.current().select(TestFiles.class).get(),TestFiles.class);
            HiEcmaScript5.prepareTemplates(this.getServletContext());

        }

        DEPLOY_ID = String.valueOf(new Date().getTime());

    }



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        doHandle(request,response,true);

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doHandle(request,response,false);

    }


    private void findControllers(){

        //TODO: Implement

    }

    private void findFrontiers(){

        //TODO: Implement

    }

    private void findAndLoadComponents() throws ServletException{


        org.reflections.Reflections reflections = new Reflections( new ConfigurationBuilder()
                .addClassLoader(this.getClass().getClassLoader())
                .setScanners(new TypeAnnotationsScanner())
                .setUrls(ClasspathHelper.forPackage("mz.co.hi.web.component"))
        );

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(WebComponent.class);
        for(Class<?> componentClass : classes){

            String scriptName = componentClass.getSimpleName().toLowerCase();
            String minifiedScriptName = componentClass.getSimpleName().toLowerCase()+".min";

            URL componentScript =  null;

            try {

                if (AppConfigurations.get().underDevelopment())
                    componentScript = getServletContext().getResource("/" + scriptName + ".js");
                else
                    componentScript = getServletContext().getResource("/" + minifiedScriptName + ".js");

            }catch (MalformedURLException ex){

                throw new HiException("Invalid component name : "+componentClass.getSimpleName(),ex);

            }

            if(componentScript==null)
                continue;

            try {

                componentsScript+=Helper.readTextStreamToEnd(componentScript.openStream(), null);
                getServletContext().log(componentClass.getSimpleName()+" Web component loaded");

            }catch (Exception ex){

                throw new HiException("Could not read component script : "+componentClass.getSimpleName(),ex);

            }


        }



    }

    private String filterRouteURL(String routeURL,HttpServletResponse response) throws IOException {


        if(routeURL.trim().length()==0){

            response.sendRedirect(AppConfigurations.get().getWelcomeUrl());
            //return AppConfigurations.get().getWelcomeUrl();
            return null;

        }

        return routeURL;

    }


    private String getRouteURL(HttpServletRequest request){

        return request.getRequestURI().replace(request.getContextPath()+"/","");

    }


    private void doHandle(HttpServletRequest request, HttpServletResponse response, boolean isPost) throws ServletException,IOException{

        HttpSession session = request.getSession();

        //String url = getURL(request);
        String routeURL = getRouteURL(request);


        routeURL = filterRouteURL(routeURL,response);
        if(routeURL==null){

            return;

        }

        RequestContext requestContext = CDI.current().select(RequestContext.class).get();
        requestContext.setRouteUrl(routeURL);
        requestContext.setResponse(response);

        //RequestContext requestContext = new RequestContext(request,response,this.getServletContext(),routeURL);
        //requestContext.setSession(session);

        //Sessions.handleUserDetails(request.getRemoteUser());

        boolean handled = false;

        if(wasPreviouslyMatched(routeURL)){

            ReqHandler reqHandler = ReqHandler.getHandler(getPreviouslyMatchedHandler(routeURL));
            reqHandler.handle(requestContext);
            return;

        }


        ReqHandler[] reqHandlers  = ReqHandler.getAllHandlers();
        for(ReqHandler reqHandler : reqHandlers){

            try {

                Class handlerClazz = ReqHandler.getHandlerClass(reqHandler);
                if (ReqHandler.matches(requestContext, handlerClazz,isPost)){
                    handled = reqHandler.handle(requestContext);

                    if(handled){

                        storeMatchedUrl(routeURL,handlerClazz);

                        break;

                    }

                }

            }catch (ServletException ex){

                requestContext.getResponse().sendError(500);
                throw ex;

            }

        }

        if(!handled){

            requestContext.getResponse().sendError(404);

        }


    }


    private  boolean wasPreviouslyMatched(String route){

        boolean wasIt = false;

        synchronized (matchedUrls){

            wasIt = matchedUrls.containsKey(route);

        }

        return wasIt;

    }

    private synchronized String getPreviouslyMatchedHandler(String route){

        String previousHandler = null;

        synchronized (matchedUrls){

            previousHandler = matchedUrls.get(route);

        }

        return previousHandler;

    }

    private synchronized void storeMatchedUrl(String routeURL, Class<? extends ReqHandler> clazz){

        synchronized (matchedUrls){

            matchedUrls.put(routeURL,clazz.getCanonicalName());

        }

    }





}
