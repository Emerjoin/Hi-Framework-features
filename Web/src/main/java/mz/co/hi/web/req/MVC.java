package mz.co.hi.web.req;


import mz.co.hi.web.HiCDI;
import mz.co.hi.web.RequestContext;
import mz.co.hi.web.mvc.ClassLoader;
import mz.co.hi.web.AppContext;
import mz.co.hi.web.mvc.HTMLizer;

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

@HandleRequests(regexp = "[a-zA-Z-]{2,}\\/[a-zA-Z-]{2,}")
@ApplicationScoped
public class MVC extends ReqHandler{

    private RequestContext requestContext = null;

    @Inject
    private AppContext appContext;

    private static char[] alphabet = new char[]{'A','B','C','D','E','F','G','H','I','J',
            'K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

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
        requestContext.getData().put("controllerU",controller);
        controller = getControllerClassFromURLPart(controller);

        String action = mvcUrl.substring(indexSlash+1,mvcUrl.length());
        requestContext.getData().put("actionU",action);
        action = getActionMethodFromURLPart(action);


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


            if(!ReqHandler.userHasPermission(controller,actionMethod,requestContext)){

                try {

                    requestContext.getResponse().sendError(403);

                }catch (Exception ex){

                    requestContext.getServletContext().log("Failed no send 403 error",ex);
                    return false;

                }

            }

            actionMethod.setAccessible(true);

            Object instance = null;

            HiCDI.shouldHaveCDIScope(controller);

            try {


                //TODO: Consider that CDI wont be present sometimes

                instance = CDI.current().select(controller).get();



            }catch (Exception ex){

                throw new ServletException("Injection of controller <"+controller.getCanonicalName()+"> failed",ex);

            }



            if(withParams)
                actionMethod.invoke(instance,getValues(requestContext.getRequest()));
            else
                actionMethod.invoke(instance);

            return true;

        }catch (NoSuchMethodException ex){

            return false;

        }catch (InvocationTargetException e2 ) {

            e2.printStackTrace();
            throw new ServletException("Exception thrown while invoking action <" + action + "> on controller <" + controller.getCanonicalName() + ">", e2);


        }catch (IllegalAccessException e3){

            e3.printStackTrace();
            throw new ServletException("Could not access constructor of Controller <"+controller.getCanonicalName()+">",e3);

        }


    }



    private static String noHyphens(String urlToken){

        if(urlToken==null)
            return null;

        char[] tokenChars = urlToken.toCharArray();
        StringBuilder hyphenLessToken = new StringBuilder();

        boolean capitalizeNext=false;

        for(char character:  tokenChars){

            if(capitalizeNext==true) {
                hyphenLessToken.append(Character.toUpperCase(character));
                capitalizeNext = false;
                continue;
            }



            if(character=='-') {

                capitalizeNext = true;
                continue;

            }


            hyphenLessToken.append(character);


        }


        return hyphenLessToken.toString();

    }

    public static String getControllerClassFromURLPart(String urlPart){

         String capitalized = urlPart.substring(0,1).toLowerCase()
                 +urlPart.substring(1,urlPart.length());

        return noHyphens(capitalized);

    }

    public static String getActionMethodFromURLPart(String urlPart){

        return noHyphens(urlPart);

    }


    public static String getURLController(String clazz){

        StringBuilder alphabetStr = new StringBuilder();
        alphabetStr.append(alphabet);

        char[] controllerChars = clazz.toCharArray();

        StringBuilder urlController = new StringBuilder();
        urlController.append(controllerChars[0]);


        for(int i=1;i<controllerChars.length;i++){

            StringBuilder stringBuilder = new StringBuilder();
            char character = controllerChars[i];
            stringBuilder.append(character);

            //It is a capital character
            if(alphabetStr.indexOf(stringBuilder.toString())!=-1)
                urlController.append('-');


            urlController.append(character);

        }

        return urlController.toString().toLowerCase();


    }

}
