package mz.co.hi.web;

import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.ConfigProvider;
import mz.co.hi.web.boot.BootAgent;
import mz.co.hi.web.internal.Logging;
import mz.co.hi.web.internal.Router;
import mz.co.hi.web.uti.Timing;
import org.slf4j.Logger;

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

import static mz.co.hi.web.uti.Timing.*;

/**
 * Created by Mario Junior.
 */
@WebServlet(urlPatterns = "/*",name = "Hi-Framework-Dispatcher-Servlet",loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    private static Logger _log = Logging.getInstance().getLogger();

    @Inject
    private BootAgent bootAgent;

    @Inject
    private Router router;

    @Inject
    private ConfigProvider configProvider;

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

        try {

            tic();

            HttpSession session = request.getSession();
            String routeURL = getRouteURL(request);

            routeURL = filterRouteURL(routeURL, response);
            if (routeURL == null)
                return;

            RequestContext requestContext = CDI.current().select(RequestContext.class).get();
            requestContext.setRouteUrl(routeURL);
            requestContext.setResponse(response);
            router.doRoute(requestContext, routeURL, isPost);

        }finally {

            _log.debug(String.format("Request handled in %f milliseconds",toc()));

        }

    }


    public void init() throws ServletException{

        _log.info("---Hi-Framework is Booting up...");
        bootAgent.init(getServletContext(),getServletConfig());
        _log.info("---Boot complete!");

    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        doHandle(request,response,true);

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doHandle(request,response,false);

    }





}
