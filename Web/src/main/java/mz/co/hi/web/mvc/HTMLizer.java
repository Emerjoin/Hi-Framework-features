package mz.co.hi.web.mvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mz.co.hi.web.*;
import mz.co.hi.web.AppContext;
import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.mvc.exceptions.ConversionFailedException;
import mz.co.hi.web.mvc.exceptions.MalMarkedTemplateException;
import mz.co.hi.web.mvc.exceptions.NoSuchTemplateException;
import mz.co.hi.web.mvc.exceptions.TemplateException;

import javax.enterprise.inject.spi.CDI;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class HTMLizer {

    private static HTMLizer instance = null;
    private static final String PREPARE_NEXT_VIEW = "Hi.$ui.js.createViewScope(vpath,context_vars,viewToLoad.html,false,false,false,false);";
    private static final String RUN_APP = "Hi.$angular.run();";
    private static final String BOOSTRAP_ANGULAR = "angular.bootstrap(document, [\"hi\"]);";
    //private static final String VIEW_NG = "<div id='app-view' ng-controller=\"view_controller\"></div>";
    private static final String VIEW_NG = "<div id='app-view'></div>";

    public static final String JS_INVOCABLES_KEY = "$invoke";
    public static final String TEMPLATE_DATA_KEY = "$root";

    private GsonBuilder gsonBuilder = null;

    public void setGsonBuilder(GsonBuilder builder){

        this.gsonBuilder = builder;

    }



    public static HTMLizer getInstance(){


        return new HTMLizer();

    }

    private RequestContext requestContext = null;

    private ActiveUser activeUser = null;


    public HTMLizer(){

        this.requestContext = CDI.current().select(RequestContext.class).get();

    }

    public void setRequestContext(RequestContext requestContext){


        this.requestContext = requestContext;

    }


    private String fetchTemplate(FrontEnd frontEnd) throws TemplateException {

        String templateName = frontEnd.getTemplate();


        URL templateURL = null;

        try {

            templateURL = requestContext.getServletContext().getResource("/" + templateName);

        }catch (Exception ex){

            throw new NoSuchTemplateException(templateName);

        }

        if(templateURL==null){

            throw new NoSuchTemplateException(templateName);

        }


        String templateFileContent = "";

        try {

            templateFileContent = Helper.readTextStreamToEnd(templateURL.openStream(), null);

        }catch (Exception ex){

            throw new NoSuchTemplateException(templateName);

        }

        //Check the body tag, as its going to be replaced later in this same method
        int bodyCloseIndex = templateFileContent.indexOf("</body>");
        if(bodyCloseIndex==-1){

            throw new MalMarkedTemplateException(templateName,"token </body> could not be found. Body tag might not be present.");

        }


        int headCloseIndex = templateFileContent.indexOf("</head>");
        if(headCloseIndex==-1){

            throw new MalMarkedTemplateException(templateName,"token </head> could not be found. Head tag might not be present");

        }

        //Check the view_content div
        int viewContentDivByIdIndex = templateFileContent.indexOf("id=\"view_content\"");
        int viewContentDivByBrackets = templateFileContent.indexOf("{{view_content}}");

        if(viewContentDivByIdIndex==-1){

            throw new MalMarkedTemplateException(templateName,"no element with id property set to \"view_content\" could be found");

        }

        if(viewContentDivByBrackets==-1){

            throw new MalMarkedTemplateException(templateName,"token {{view_content}} could not be found.");


        }


        templateFileContent = AppConfigurations.get().getTunnings().applySmartCaching(templateFileContent);
        return templateFileContent;


    }


    private InputStream openLoaderJS(){

        URL loaderJS = null;
        InputStream inputStream = null;

        try {

            loaderJS = requestContext.getServletContext().getResource("/loader.js");

        }catch (Exception ex){

            //TODO: Do something about

        }

        if(loaderJS!=null){

            try{

                inputStream = loaderJS.openStream();

            }catch (Exception ex){

                //TODO: Do something about

            }

        }

        return inputStream;

    }



    private String getNg(){

        return  "<div id='app-view' ng-controller=\"view_controller\"></div>";

    }

    private String javascriptVar(String name, String value){

        if(name==null){

            return null;

        }

        if(name.trim().length()==0){

            return null;

        }


        String declaration="var "+name+"="+value.toString()+";";
        return declaration;

    }

    private String makeJavascript(String code){

        return "<script>"+code+"</script>";

    }

    private String makeFunction(String name,String code){

        return "function "+name+"(){"+code+"}";

    }


    private String getInitSnnipet(){

        //return getNextClosureInformation()+getNextViewPath()+getViewJs()+getControllerSetter();
        return getNextClosureInformation()+getViewJs()+getControllerSetter();

    }


    private String getLoaderJavascript(FrontEnd frontEnd){

        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append(" var appLang = "+ frontEnd.getLangDictionary()+";");
        scriptBuilder.append(" var appLangName = '"+ frontEnd.getLanguage()+"';");
        scriptBuilder.append("if(typeof loadApp==\"function\"){");
        scriptBuilder.append("if (window.addEventListener)");
        scriptBuilder.append(" window.addEventListener(\"load\", loadApp, false);");
        scriptBuilder.append(" else if (window.attachEvent)");
        scriptBuilder.append(" window.attachEvent(\"onload\",loadApp);");
        scriptBuilder.append("else window.onload = loadApp;");
        scriptBuilder.append(" }else{");
        scriptBuilder.append("if(typeof $ignition==\"function\"){");
        scriptBuilder.append(getInitSnnipet());//New code
        scriptBuilder.append("angular.element(document).ready(function() {");
        //scriptBuilder.append("angular.bootstrap(document, [\"hi\"]);");
        scriptBuilder.append("$ignition();");
        scriptBuilder.append("});}}");
        return scriptBuilder.toString();


    }

    private JsonObject getAppData(){

        activeUser = CDI.current().select(ActiveUser.class).get();

        CharSequence http = "http://";
        CharSequence https = "https://";

        AppContext appContext = CDI.current().select(AppContext.class).get();

        JsonObject app = Json.createObjectBuilder()
                .add("base_url", requestContext.getBaseURL())
                .add("simple_base_url", requestContext.getBaseURL().replace(http,"").replace(https,""))
                .add("deployId",appContext.getDeployId())
                .add("deployMode",appContext.getDeployMode().toString())
                .add("csrfToken",activeUser.getCsrfToken())
                .build();

        return app;

    }


    private String getNextClosureInformation(){

        String controller = requestContext.getData().get("controllerU").toString();
        String action = requestContext.getData().get("actionU").toString();
        String functionInvocation = "Hi.$nav.setNextControllerInfo(\""+controller+"\",\""+action+"\");";
        return functionInvocation;


    }

    private String getNextViewPath(){

        String controller = requestContext.getData().get("controllerU").toString();
        String action = requestContext.getData().get("actionU").toString();
        //String functionInvocation = "Hi.Internal.setNextViewPath(false,\""+controller+"\",\""+action+"\");";
        String functionInvocation = "var vpath = Hi.$nav.getViewPath(\""+controller+"\",\""+action+"\");";
        return functionInvocation;


    }

    private String getControllerSetter(){

        String controller = requestContext.getData().get("controllerU").toString();

        String action = requestContext.getData().get("actionU").toString();
        String functionInvocation = "Hi.$ui.js.setLoadedController(\""+controller+"\",\""+action+"\");";
        return functionInvocation;

    }

    private String getViewJs(){


        if(!requestContext.getData().containsKey("view_js")){

            return "Hi.view(function(_){})";

        }


        String view = requestContext.getData().get("view_js").toString();

        return view;

    }

    private boolean ignoreView(){

        return requestContext.getRequest().getHeader("Ignore-View")!=null;

    }

    private boolean ignoreJs(){

        return requestContext.getRequest().getHeader("Ignore-Js")!=null;

    }

    //TODO: WHat about debug messages
    public String process(Controller controller,boolean ignoreView) throws TemplateException, ConversionFailedException {


        //Get the action to perform from the FrontEnd
        FrontEnd frontEnd = CDI.current().select(FrontEnd.class).get();


        //Get the template
        String template = fetchTemplate(frontEnd);
        String loadedJSContent= DispatcherServlet.hiScript;


        Map viewData = (Map) requestContext.getData().get(Controller.VIEW_DATA_KEY);



        //TODO: Make sure its not null
        String viewHTML = null;
        if(requestContext.getData().containsKey("view_content")){

            viewHTML = requestContext.getData().get("view_content").toString();

            if(viewHTML!=null)
                viewHTML = AppConfigurations.get().getTunnings().applySmartCaching(viewHTML);

        }

        Map route = new HashMap();
        route.put("controller", requestContext.getData().get("controllerU").toString());
        route.put("action", requestContext.getData().get("actionU").toString());

        /* AJAX REQUEST */
        if(requestContext.hasAjaxHeader()) {


            //Client-side invocations
            Map<String,Map> actions = frontEnd.getLaterInvocations();

            //Not Empty
            if(!actions.isEmpty()){

                viewData.put(JS_INVOCABLES_KEY,actions);

            }





            Map map = new HashMap();

            if(!ignoreView()) map.put("view", viewHTML);
            if(!ignoreJs())   map.put("controller", getViewJs());
            map.put("data", viewData);
            map.put("route",route);
            map.put("response", 200);

            String resultResponse = null;

            Gson gson = gsonBuilder.create();

            try {

                resultResponse = gson.toJson(map);

            }catch (Exception ex){

                String actionMethod = requestContext.getData().get("action").toString();
                throw new ConversionFailedException(controller.getClass().getCanonicalName(),actionMethod,ex);

            }

            requestContext.getResponse().setContentType("text/json;charset=UTF8");
            requestContext.echo(resultResponse);

            return resultResponse;


        }

        if(requestContext.isUserLogged()){

            //Can only fetch data for logged user

            if(!viewData.containsKey(TEMPLATE_DATA_KEY)){

                //TODO: Re-check this
                viewData.put("$root", new HashMap<>());

            }

            Map $templateDataMap = (Map) viewData.get("$root");


            if(AppConfigurations.get().getUserDetailsProvider()!=null){



                $templateDataMap.put("$user", AppConfigurations.get().getUserDetailsProvider().
                        getDetails(requestContext.getUsername()));

            }

            viewData.put("$root",$templateDataMap);

        }




        /* NORMAL REQUEST */
        viewData.put("$route",route);
        String loaderJavascript = makeJavascript(getLoaderJavascript(frontEnd));

        if(loadedJSContent==null) {

            loadedJSContent="<!--Empty loaded-->";

        }else{

            String tokenToReplace = "//_place_init_code_here";
            String replacement = getInitSnnipet();
            loadedJSContent = loadedJSContent.replace(tokenToReplace,replacement);
        }


        CharSequence bodyCloseTag = "</body>";
        template = template.replace(bodyCloseTag,"</body>"+loaderJavascript);

        CharSequence headCloseTag = "</head>";
        CharSequence headClosedScript = "</head>"+makeJavascript(loadedJSContent);
        template = template.replace(headCloseTag,headClosedScript);


        Gson gson = new GsonBuilder().create();
        String viewDataStr = null;

        try{

            viewDataStr = gson.toJson(viewData);

        }catch (Exception ex){

            String actionMethod = requestContext.getData().get("action").toString();
            throw new ConversionFailedException(controller.getClass().getCanonicalName(),actionMethod,ex);

        }


        Map html = new HashMap();
        html.put("html",viewHTML);

        String appVariable = makeJavascript(javascriptVar("App",getAppData().toString()));
        String contextVarsVariable = makeJavascript(javascriptVar("context_vars",viewDataStr));
        String startupScript = makeJavascript(makeFunction("$startup",getNextViewPath()+PREPARE_NEXT_VIEW));
        String ignitionScript = makeJavascript(makeFunction("$ignition",RUN_APP+BOOSTRAP_ANGULAR));
        String viewToLoadVariable = makeJavascript(javascriptVar("viewToLoad",gson.toJson(html)));


        String allJson = appVariable+contextVarsVariable+viewToLoadVariable+startupScript+ignitionScript;


        allJson+="\n"+VIEW_NG;
        CharSequence toReplace = "{{view_content}}";
        CharSequence replaceBy = allJson;
        String processedResult =  template.replace(toReplace,replaceBy);


        this.filter(processedResult);
        this.sendToClient(processedResult);

        return processedResult;


    }

    private void filter(String result){

        result="<h1>Changed</h1>";

    }


    private void sendToClient(String result){

        requestContext.getResponse().setHeader("Cache-Control", "no-cache");
        requestContext.getResponse().setContentType("text/html;charset=UTF8");
        Helper.echo(result, requestContext);

    }

    private void applySmartCaching(String markup){



    }


}
