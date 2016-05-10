package com.web.yayee.req;

import com.web.yayee.RequestContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mario Junior.
 */
public abstract class ReqHandler {

    public abstract boolean handle(RequestContext requestContext) throws ServletException, IOException;

    private static List<ReqHandler> reqHandlers = new ArrayList<ReqHandler>();
    private static Map<String,ReqHandler> handlers = new HashMap();
    private static Map<ReqHandler,Class> handlersClasses = new HashMap();

    public static void register(ReqHandler reqHandler,Class<? extends ReqHandler> clazz){

        reqHandlers.add(reqHandler);
        handlers.put(clazz.getCanonicalName(),reqHandler);
        handlersClasses.put(reqHandler,clazz);

    }

    public static ReqHandler getHandler(String className){

        if(handlers.containsKey(className)){

            return handlers.get(className);

        }

        return null;

    }

    public static Class<? extends ReqHandler> getHandlerClass(ReqHandler reqHandler){

        return  handlersClasses.get(reqHandler);

    }

    public static ReqHandler[] getAllHandlers(){

        ReqHandler[] allReqHandlers = new ReqHandler[reqHandlers.size()];
        reqHandlers.toArray(allReqHandlers);
        return allReqHandlers;

    }

    public static boolean matches(RequestContext requestContext, Class<? extends ReqHandler> reqHandler, boolean post) throws ReqMatchException{



        Annotation annotation = reqHandler.getDeclaredAnnotation(HandleRequests.class);
        if(annotation==null){

            throw new ReqMatchException(reqHandler.getCanonicalName(),"handler <"+reqHandler.getCanonicalName()+"> is not annoted");

        }


        String url = requestContext.getRouteUrl();
        HandleRequests handleRequests = (HandleRequests) annotation;

        if(post){


            if(!handleRequests.supportPostMethod()){

                return false;

            }

        }


        String regex = handleRequests.regexp();
        try {

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(url);
            return matcher.matches();


        }catch (Exception ex){

            throw new ReqMatchException(reqHandler.getCanonicalName(), "handler <"+reqHandler.getCanonicalName()+"> has an invalid match regular expression");

        }


    }

    private static String getUsefullUrl(HttpServletRequest request){

        String url = request.getRequestURI().replace(request.getContextPath()+"/","");
        return url;


    }

}
