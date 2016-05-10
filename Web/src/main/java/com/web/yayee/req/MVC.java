package com.web.yayee.req;


import com.web.yayee.RequestContext;
import com.web.yayee.ClassLoader;
import com.web.yayee.app.AppContext;
import com.web.yayee.mvc.Controller;
import com.web.yayee.mvc.HTMLizer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@HandleRequests(regexp = "[a-zA-Z]{2,}\\/[a-zA-Z_]{2,}")
@ApplicationScoped
public class MVC extends ReqHandler{

    private RequestContext requestContext = null;

    @Inject
    private AppContext appContext;

    private static HashMap<String,String> templates = new HashMap<String, String>();

    public static void storeTemplate(String name,String content){

        templates.put(name,content);

    }

    @Produces
    public HTMLizer getHTMLizer(){

        HTMLizer htmLizer = HTMLizer.getInstance();
        htmLizer.setGsonBuilder(appContext.getGsonBuilder());
        return htmLizer;

    }

    public static String getTemplate(String name){

        return templates.get(name);

    }

    public boolean handle(RequestContext requestContext) throws ServletException, IOException {
        this.requestContext = requestContext;

        String mvcUrl = requestContext.getRouteUrl();
        int indexSlash = mvcUrl.indexOf('/');
        String controller = mvcUrl.substring(0,indexSlash);
        String action = mvcUrl.substring(indexSlash+1,mvcUrl.length());

        Class controllerClass= ClassLoader.getInstance().findController(controller);
        if(controllerClass==null){

            return false;

        }

        boolean actionFound = false;

        requestContext.getData().put("action",action);
        requestContext.getData().put("controller",controller);

        if(controllerClass!=null){

            actionFound = callAction(action,controllerClass, requestContext);

        }


        return actionFound;


    }


    private Map getValues(HttpServletRequest request){

        Map<String,Object> finalValues = new HashMap<String, Object>();
        Map<String,String[]> map =  request.getParameterMap();

        for(String key : map.keySet()){

            String[] values = map.get(key);
            if(values.length==1){

                finalValues.put(key,values[0]);

            }else{

                finalValues.put(key,values);

            }

        }

        return finalValues;

    }

    private boolean callAction(String action, Class controller,RequestContext requestContext) throws ServletException{

        try {


            Method actionMethod = null;
            boolean withParams = true;

            try {

                actionMethod = controller.getMethod(action, Map.class);

            }catch (Exception ex){

                withParams = false;
                actionMethod = controller.getMethod(action, null);

            }

            actionMethod.setAccessible(true);

            Object instance = null;


            try {


                Object bean = CDI.current().select(controller).get();

                if(bean==null){

                    instance = controller.newInstance();

                }else{


                    instance = bean;


                }

            }catch (Exception ex){

                ex.printStackTrace(System.out);

                instance = controller.newInstance();

            }



            Method setRequestContextMethod = null;

            try {


                setRequestContextMethod = Controller.class.getDeclaredMethod("setRequestContext",RequestContext.class);
                setRequestContextMethod.invoke(instance,requestContext);


            }catch (Exception ex){

                ex.printStackTrace(System.err);


            }

            //Controller controllerInstance = (Controller) instance;
            //controllerInstance.setRequestContext(requestContext);


            if(withParams)
                actionMethod.invoke(instance,getValues(requestContext.getRequest()));
            else
                actionMethod.invoke(instance);

            return true;

        }catch (NoSuchMethodException ex){

            return false;

        }catch (InvocationTargetException e2) {

                e2.printStackTrace();
                throw new ServletException("Exception thrown while invoking action <"+action+"> on controller <"+controller.getCanonicalName()+">",e2);


        }catch (InstantiationException ex){

            ex.printStackTrace();
            throw new ServletException("Could not instantiate the Controller <"+controller.getCanonicalName()+">",ex);

        }catch (IllegalAccessException e3){

            e3.printStackTrace();
            throw new ServletException("Could not access contructor of Controller <"+controller.getCanonicalName()+">",e3);

        }



    }

}
