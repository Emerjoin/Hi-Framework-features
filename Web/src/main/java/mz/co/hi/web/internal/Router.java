package mz.co.hi.web.internal;

import mz.co.hi.web.RequestContext;
import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.exceptions.HiException;
import mz.co.hi.web.req.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Mário Júnior
 */
@ApplicationScoped
public class Router {


    private HashMap<String,String> matchedUrls = new HashMap();

    public void init(ServletContext context, ServletConfig config) throws HiException{

        if(AppConfigurations.get()!=null){
            ReqHandler.register(CDI.current().select(MVC.class).get(),MVC.class);
            ReqHandler.register(CDI.current().select(Assets.class).get(),Assets.class);
            ReqHandler.register(CDI.current().select(HiEcmaScript5.class).get(),HiEcmaScript5.class);
            ReqHandler.register(CDI.current().select(Frontiers.class).get(),Frontiers.class);
            ReqHandler.register(CDI.current().select(Tests.class).get(),Tests.class);
            ReqHandler.register(CDI.current().select(TestFiles.class).get(),TestFiles.class);

            try {

                HiEcmaScript5.prepareTemplates(context);


            }catch (ServletException ex){

                throw new HiException("Failed to prepare templates",ex);

            }
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

    public void doRoute(RequestContext requestContext, String routeURL) throws ServletException{

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



}
