package mz.co.hi.web;

import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.ConfigurationsAgent;
import mz.co.hi.web.config.ConfigSection;
import mz.co.hi.web.events.listeners.ControllerCallsListener;
import mz.co.hi.web.events.listeners.FrontierCallsListener;
import mz.co.hi.web.events.listeners.TemplateLoadListener;
import mz.co.hi.web.req.*;
import org.jboss.jandex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mario Junior.
 */
@WebServlet(urlPatterns = "/*",name = "HiServlet",loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    private HashMap<String,String> matchedUrls = new HashMap();

    private static boolean initialized = false;

    public static TemplateLoadListener templateLoadListener = null;
    public static ControllerCallsListener controllerCallsListener = null;
    public static FrontierCallsListener frontierCallsListener = null;

    public static String LOGGER = "hi-web";
    private static Logger _log = null;



    public DispatcherServlet(){



    }










    //TODO: Refactor
    public void init(ServletConfig config) throws ServletException{

        if(initialized){

            return;

        }


        String logger = config.getInitParameter("logger");
        if(logger==null)
            logger = "hi-web";

        ConfigurationsAgent.setLogger(logger);
        _log = LoggerFactory.getLogger(logger);

        //TODO: Use this to map exceptions to documentation links and display them when throwing exception
        String documentationPath =  config.getInitParameter("docs");


        initialized = true;

        _log.info("---Initializing Hi-Framework servlet...");

        try{

            Instance<TemplateLoadListener> loadListenerInstance = CDI.current().select(TemplateLoadListener.class);
            if(loadListenerInstance.isUnsatisfied())
                _log.warn(String.format("No implementation of %s found",TemplateLoadListener.class.getSimpleName()));
            else {
                templateLoadListener = loadListenerInstance.get();
                _log.info(String.format("%s detected : %s",TemplateLoadListener.class.getSimpleName(),templateLoadListener.getClass().getCanonicalName()));
            }

        }catch (Throwable ex){

            _log.error(String.format("Failed to get a %s instance",TemplateLoadListener.class.getSimpleName()),ex);

        }

        try{

            Instance<ControllerCallsListener> controllerCallsListenerInstance =
                    CDI.current().select(ControllerCallsListener.class);

            if(controllerCallsListenerInstance.isUnsatisfied())
                _log.warn(String.format("No implementation of %s found",ControllerCallsListener.class.getSimpleName()));
            else {

                controllerCallsListener = controllerCallsListenerInstance.get();
                _log.info(String.format("%s detected : %s",ControllerCallsListener.class.getSimpleName(),controllerCallsListener.getClass().getCanonicalName()));

            }

        }catch (Throwable ex){

            _log.error(String.format("Failed to get a %s instance",ControllerCallsListener.class.getSimpleName()),ex);

        }


        try{


            Instance<FrontierCallsListener> frontierCallsListenerInstance
                    = CDI.current().select(FrontierCallsListener.class);

            if(frontierCallsListenerInstance.isUnsatisfied())
                _log.warn(String.format("No implementation of %s found",FrontierCallsListener.class.getSimpleName()));
            else{

                frontierCallsListener = frontierCallsListenerInstance.get();
                _log.info(String.format("%s detected : %s",FrontierCallsListener.class.getSimpleName(),frontierCallsListenerInstance.getClass().getCanonicalName()));

            }

        }catch (Throwable ex){

            _log.error(String.format("Failed to get a %s instance",FrontierCallsListener.class.getSimpleName()),ex);

        }


        readLibScript();
        readJavascriptInit();
        readLoaderScript();
        readGenericFrontier();
        findControllersAndMap();
        generateFrontiers();
        findAndLoadComponents();
        findTestedActions();
        initBootExtensions();

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

        _log.info("---Finished Hi-Framework servlet initialization...");




    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        doHandle(request,response,true);

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doHandle(request,response,false);

    }







    private String filterRouteURL(String routeURL,HttpServletResponse response) throws IOException {


        if(routeURL.trim().length()==0){

            if(AppConfigurations.get().getWelcomeUrl()!=null) {

                response.sendRedirect(AppConfigurations.get().getWelcomeUrl());

                return null;

            }


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
