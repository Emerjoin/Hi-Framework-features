package mz.co.hi.web.req;

import com.google.gson.*;
import mz.co.hi.web.ActiveUser;
import mz.co.hi.web.FrontEnd;
import mz.co.hi.web.RequestContext;
import mz.co.hi.web.AppContext;
import mz.co.hi.web.frontier.*;
import mz.co.hi.web.frontier.exceptions.InvalidFrontierParamException;
import mz.co.hi.web.frontier.exceptions.MapConversionException;
import mz.co.hi.web.frontier.exceptions.MissingFrontierParamException;
import mz.co.hi.web.frontier.model.FrontierClass;
import mz.co.hi.web.frontier.model.FrontierMethod;
import mz.co.hi.web.frontier.model.MethodParam;
import mz.co.hi.web.mvc.HTMLizer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

@HandleRequests(regexp = "f.m.call/[$_A-Za-z0-9]+/[$_A-Za-z0-9]+", supportPostMethod = true)
@ApplicationScoped
public class Frontiers extends ReqHandler {

    private static Map<String,FrontierClass> frontiersMap = new HashMap();

    @Inject
    private AppContext appContext;

    @Inject
    private FrontEnd frontEnd;

    @Inject
    private ServletContext servletContext;

    @Inject
    private RequestContext requestContext;

    @Inject
    private ActiveUser activeUser;

    public static void addFrontier(FrontierClass frontierClass){

        frontiersMap.put(frontierClass.getSimpleName(),frontierClass);

    }

    public static boolean frontierExists(String name){

        return frontiersMap.containsKey(name);

    }

    public static FrontierClass getFrontier(String name){

        return frontiersMap.get(name);

    }


    private Object getParamValue(String frontier,FrontierMethod frontierMethod, MethodParam methodParam,
                                 Map<String,Object> uploadsMap,Map<String,Object> argsMap,
                                 HttpServletRequest request) throws Exception{

        Object paramValue = argsMap.get(methodParam.getName());
        if(paramValue==null)
            throw new MissingFrontierParamException(frontier,frontierMethod.getName(),methodParam.getName());

        else{

            if(paramValue instanceof String){

                String strParamValue = (String) paramValue;

                if(strParamValue.startsWith("$$$upload")){

                    //Get the name of the upload group
                    String uploadName = strParamValue.substring(strParamValue.indexOf(":")+1,strParamValue.length());

                    //Get the total uploaded files

                    //Fetch each of the uploaded files


                    //Create the FileUpload object instances


                }

            }

        }

    }


    private Map matchParams(String frontier,FrontierMethod frontierMethod, RequestContext requestContext) throws MissingFrontierParamException, InvalidFrontierParamException {

        //TODO: Handle exceptions correctly

        HttpServletRequest req =  requestContext.getRequest();

        //Invocation with files detected
        if(req.getContentType().contains("multipart/form-data")){

            try {

                Part uploadsPart = req.getPart("$uploads");
                Scanner uploadsScanner = new Scanner(uploadsPart.getInputStream(),"UTF-8");
                StringBuilder uploadsJSONStringBuilder = new StringBuilder();
                while (uploadsScanner.hasNextLine())
                    uploadsJSONStringBuilder.append(uploadsScanner.nextLine());

                Part argsPart = req.getPart("$args");
                Scanner argsScanner = new Scanner(argsPart.getInputStream(),"UTF-8");
                StringBuilder argsJSONStringBuilder = new StringBuilder();
                while (argsScanner.hasNextLine())
                    argsJSONStringBuilder.append(argsScanner.nextLine());

                Gson gson = new Gson();
                Map<String,Object> uploadsMap = gson.fromJson(uploadsJSONStringBuilder.toString(),Map.class);
                Map<String,Object> argsMaps = gson.fromJson(argsJSONStringBuilder.toString(),Map.class);


                MethodParam methodParams[] = frontierMethod.getParams();

                for(MethodParam methodParam : methodParams){





                }



            }catch (IOException | ServletException ex){

                ex.printStackTrace();

            }

        }


        StringBuilder stringBuilder = new StringBuilder();

        try {

            Scanner scanner = new Scanner(requestContext.getRequest().getInputStream());
            while (scanner.hasNextLine()) {

                stringBuilder.append(scanner.nextLine());

            }

        }catch (Exception ex){

            return null;

        }

        Map map =  new HashMap();
        Gson gson = appContext.getGsonBuilder().create();

        JsonElement jsonEl = new JsonParser().parse(stringBuilder.toString());

        JsonObject jsonObject = jsonEl.getAsJsonObject();
        MethodParam methodParams[] = frontierMethod.getParams();

        for(MethodParam methodParam : methodParams){

            JsonElement jsonElement = jsonObject.get(methodParam.getName());
            if(jsonElement==null){

                throw new MissingFrontierParamException(frontier,frontierMethod.getName(),methodParam.getName());

            }

            Object paramValue = null;

            try {

                paramValue = gson.fromJson(jsonElement, methodParam.getType());

            }catch (Exception ex){

                paramValue = null;

            }


            if(paramValue==null){

                throw new InvalidFrontierParamException(frontier,frontierMethod.getName(),methodParam.getName());

            }

            map.put(methodParam.getName(),paramValue);

        }


        return map;

    }


    private String[] getFrontierPair(RequestContext context){

        String route = context.getRouteUrl();

        int firstSlashIndex = route.indexOf('/');
        int lastSlashIndex = route.lastIndexOf('/');

        String className = route.substring(firstSlashIndex+1,lastSlashIndex);
        String methodName = route.substring(lastSlashIndex+1,route.length());

        return new String[]{className,methodName};

    }

    @Override
    public boolean handle(RequestContext requestContext) throws ServletException, IOException {

        if(!isAuthenticRequest(requestContext))
            return false;

        String[] frontierPair = getFrontierPair(requestContext);

        String invokedClass = frontierPair[0];
        String invokedMethod = frontierPair[1];



        if(invokedClass==null||invokedMethod==null){

            return false;

        }

        if(frontierExists(invokedClass)){

            FrontierClass frontierClass = getFrontier(invokedClass);
            if(!frontierClass.hasMethod(invokedMethod)){

                return false;

            }



            FrontierMethod frontierMethod = frontierClass.getMethod(invokedMethod);
            Map params = matchParams(invokedClass,frontierMethod, requestContext);

            FrontierInvoker frontierInvoker = new FrontierInvoker(requestContext,frontierClass,frontierMethod,params);

            boolean invoked_successfully = false;

            try {



                if(!ReqHandler.userHasPermission(frontierClass.getObject().getClass(),frontierMethod.getMethod(),requestContext)){

                    requestContext.getResponse().sendError(403);
                    return true;

                }

                invoked_successfully = frontierInvoker.invoke();

            }catch (Exception ex){

                servletContext.log("An error occurred during frontier method invocation <"+invokedClass+"."+invokedMethod+">",ex);//Log the error;


                if(ex instanceof ConstraintViolationException){


                    ConstraintViolationException violationException = (ConstraintViolationException) ex;
                    Set<ConstraintViolation<?>> violationSet = violationException.getConstraintViolations();
                    String[] messages = new String[violationSet.size()];

                    int i = 0;
                    for(ConstraintViolation violation: violationSet){

                        messages[i] = violation.getMessage();
                        i++;

                    }

                    Map map = new HashMap<>();
                    Map exception = new HashMap<>();
                    exception.put("messages",messages);
                    map.put("$exception",exception);

                    Gson gson = appContext.getGsonBuilder().create();
                    String resp = gson.toJson(map);
                    requestContext.getResponse().setStatus(500);
                    requestContext.getResponse().setContentType("text/json;charset=UTF8");
                    requestContext.echo(resp);
                    return true;

                }


                requestContext.getResponse().setStatus(500);
                requestContext.getResponse().setContentType("text/json;charset=UTF8");
                return true;

            }



            if(invoked_successfully){

                try {


                    Gson gson = appContext.getGsonBuilder().create();

                    Map map = new HashMap();

                    Object returnedObject = frontierInvoker.getReturnedObject();
                    map.put("result",returnedObject);

                    if(frontEnd.gotLaterInvocations()) {

                        map.put(HTMLizer.JS_INVOCABLES_KEY, frontEnd.getLaterInvocations());

                    }

                    if(frontEnd.wasTemplateDataSet()){

                        map.put(HTMLizer.TEMPLATE_DATA_KEY,frontEnd.getTemplateData());

                    }


                    String resp = gson.toJson(map);
                    requestContext.getResponse().setContentType("text/json;charset=UTF8");
                    requestContext.echo(resp);

                }catch (Exception ex){

                    throw new MapConversionException(frontierClass.getClassName(),frontierMethod.getName(),ex);

                }

            }


            return invoked_successfully;

        }


        return false;

    }

    private boolean isAuthenticRequest(RequestContext requestContext){

        String token = requestContext.getRequest().getHeader("csrfToken");
        if(token==null)
            return false;

        return token.equals(activeUser.getCsrfToken());

    }

}
