package mz.co.hi.web;

import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.ConfigProvider;
import mz.co.hi.web.config.XMLConfigProvider;
import mz.co.hi.web.events.listeners.ControllerCallsListener;
import mz.co.hi.web.events.listeners.FrontierCallsListener;
import mz.co.hi.web.events.listeners.TemplateLoadListener;
import mz.co.hi.web.internal.BootAgent;
import mz.co.hi.web.internal.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Mario Junior.
 */
@WebServlet(urlPatterns = "/*",name = "HiServlet",loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    private static boolean initialized = false;

    public static TemplateLoadListener templateLoadListener = null;
    public static ControllerCallsListener controllerCallsListener = null;
    public static FrontierCallsListener frontierCallsListener = null;

    public static String LOGGER = "hi-web";
    private static Logger _log = null;


    @Inject
    private BootAgent bootAgent;

    @Inject
    private Router router;

    @Inject
    private ConfigProvider configProvider;

    public DispatcherServlet(){



    }



    //TODO: Refactor
    public void init(ServletConfig config) throws ServletException{


        //TODO: Use this to map exceptions to documentation links and display them when throwing exception
        String documentationPath =  config.getInitParameter("docs");

        initialized = true;
        bootAgent.init(getServletContext(),getServletConfig());
        configProvider.getLogger().info("---Boot complete...");


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
        router.doRoute(requestContext,routeURL,isPost);

    }








}
