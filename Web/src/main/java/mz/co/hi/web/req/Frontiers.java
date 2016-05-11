package mz.co.hi.web.req;

import com.google.gson.*;
import mz.co.hi.web.RequestContext;
import mz.co.hi.web.app.AppContext;
import mz.co.hi.web.frontier.MapConversionException;
import mz.co.hi.web.frontier.FrontierInvoker;
import mz.co.hi.web.frontier.InvalidFrontierParamException;
import mz.co.hi.web.frontier.MissingFrontierParamException;
import mz.co.hi.web.frontier.model.FrontierClass;
import mz.co.hi.web.frontier.model.FrontierMethod;
import mz.co.hi.web.frontier.model.MethodParam;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Mario Junior.
 */
//@HandleRequests(regexp = "com.yayee.javascript.bean.invocation\\/[A-Za-z0-9]{5,}", supportPostMethod = true)
@HandleRequests(regexp = "com.yayee.frontiers.bean.invocation\\/[A-Za-z0-9.]{5,}", supportPostMethod = true)
@ApplicationScoped
public class Frontiers extends ReqHandler {

    private static Map<String,FrontierClass> frontiersMap = new HashMap();

    @Inject
    private AppContext appContext;

    public static void addFrontier(FrontierClass frontierClass){

        frontiersMap.put(frontierClass.getSimpleName(),frontierClass);

    }

    public static boolean frontierExists(String name){

        return frontiersMap.containsKey(name);

    }

    public static FrontierClass getFrontier(String name){

        return frontiersMap.get(name);

    }



    private Map matchParams(String frontier,FrontierMethod frontierMethod, RequestContext requestContext) throws MissingFrontierParamException, InvalidFrontierParamException {

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

    @Override
    public boolean handle(RequestContext requestContext) throws ServletException, IOException {

        String invokedClass = requestContext.getRequest().getHeader("Invoked-Class");
        String invokedMethod = requestContext.getRequest().getHeader("Invoked-Method");

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
            boolean invoked_successfully = frontierInvoker.invoke();
            if(invoked_successfully){

                try {


                    Gson gson = appContext.getGsonBuilder().create();

                    Map map = new HashMap();

                    Object returnedObject = frontierInvoker.getReturnedObject();
                    map.put("result",returnedObject);

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
}
