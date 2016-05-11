package mz.co.hi.web;

import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.ConfigLoader;
import mz.co.hi.web.frontier.Scripter;
import mz.co.hi.web.frontier.model.FrontierClass;
import mz.co.hi.web.frontier.model.BeansCrawler;
import mz.co.hi.web.mvc.MissingResourcesLibException;
import mz.co.hi.web.req.*;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;

/**
 * Created by Mario Junior.
 */
@WebServlet(urlPatterns = "/*",name = "HiServlet",loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    public static String yayeeLibScript = null;
    public static String yayeeLoaderScript = null;
    public static String genericFrontierScript = null;
    public static String javascriptInitScript="";

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
                yayeeLibScript = scriptContent;

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
                yayeeLoaderScript = scriptContent;


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


            URL res = this.getServletContext().getResource("/init.js");
            if(res!=null){

                System.out.println("Reading javascript init code file...");

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                javascriptInitScript = scriptContent;

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
                yayeeLibScript+="\n"+frontier_script;


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


        if(AppConfigurations.get()!=null){

            ReqHandler.register(CDI.current().select(MVC.class).get(),MVC.class);
            ReqHandler.register(CDI.current().select(Assets.class).get(),Assets.class);
            ReqHandler.register(CDI.current().select(HiEcmaScript5.class).get(),HiEcmaScript5.class);
            ReqHandler.register(CDI.current().select(Frontiers.class).get(),Frontiers.class);
            HiEcmaScript5.prepareTemplates(this.getServletContext());

        }

    }



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        doHandle(request,response,true);

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doHandle(request,response,false);

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

        RequestContext requestContext = new RequestContext(request,response,this.getServletContext(),routeURL);
        requestContext.setSession(session);

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
