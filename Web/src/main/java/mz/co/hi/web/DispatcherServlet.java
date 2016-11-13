package mz.co.hi.web;

import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.Bootstrap;
import mz.co.hi.web.config.ConfigSection;
import mz.co.hi.web.events.listeners.ControllerCallsListener;
import mz.co.hi.web.events.listeners.FrontierCallsListener;
import mz.co.hi.web.events.listeners.TemplateLoadListener;
import mz.co.hi.web.exceptions.HiException;
import mz.co.hi.web.extension.BootExtension;
import mz.co.hi.web.extension.BootManager;
import mz.co.hi.web.frontier.Scripter;
import mz.co.hi.web.frontier.model.FrontierClass;
import mz.co.hi.web.frontier.model.BeansCrawler;
import mz.co.hi.web.meta.Frontier;
import mz.co.hi.web.meta.Tested;
import mz.co.hi.web.meta.WebComponent;
import mz.co.hi.web.mvc.Controller;
import mz.co.hi.web.mvc.ControllersMapper;
import mz.co.hi.web.mvc.exceptions.MissingResourcesLibException;
import mz.co.hi.web.req.*;
import org.jboss.jandex.*;

import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by Mario Junior.
 */
@WebServlet(urlPatterns = "/*",name = "HiServlet",loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    public static String hiTestsScript = "";
    public static String hiLibScript = null;
    public static String hiScript = null;
    public static String genericFrontierScript = null;
    public static String javascriptConfigScript ="";
    public static String frontiersScript ="";
    public static String angularJsScript ="";
    public static String componentsScript="";

    protected static  String DEPLOY_ID="";

    private HashMap<String,String> matchedUrls = new HashMap();

    private static boolean initialized = false;

    public static TemplateLoadListener templateLoadListener = null;
    public static ControllerCallsListener controllerCallsListener = null;
    public static FrontierCallsListener frontierCallsListener = null;

    public DispatcherServlet(){



    }


    private void readLibScript() throws MissingResourcesLibException {

        try {


            System.out.println("Reading main client-side code file...");
            URL res = this.getServletContext().getResource("/hi.js");
            if(res!=null){

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                hiLibScript = scriptContent;

            }


            res = this.getServletContext().getResource("/hi-tests.js");
            if(res!=null){

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                hiTestsScript = scriptContent;

            }

            res = this.getServletContext().getResource("/angular.min.js");
            if(res!=null){

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                angularJsScript = scriptContent;

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
                hiScript = scriptContent;


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


            URL res = this.getServletContext().getResource("/run.js");
            if(res!=null){

                System.out.println("Reading javascript configurations code file...");

                InputStream inputStream = res.openStream();
                String scriptContent = Helper.readTextStreamToEnd(inputStream,null);
                javascriptConfigScript = scriptContent;

            }


        }catch (Exception ex){

            throw new MissingResourcesLibException(ex);

        }


    }



    private void initBootExtensions() throws HiException{

        Set<Index> indexSet = BootstrapUtils.getIndexes(getServletContext());
        if(indexSet==null)
            return;

        Iterable<BootExtension> bootExtensions = BootManager.getExtensions();
        this.getServletContext().log("Initializing boot extensions...");

        for(BootExtension extension : bootExtensions)
            extension.boot(indexSet);


    }



    private void generateFrontiers() throws ServletException {

        List beansList = new ArrayList();
        this.getServletContext().log("Looking for frontiers...");

        Set<Index> indexSet = BootstrapUtils.getIndexes(getServletContext());
        if(indexSet==null)
            return;

        for(Index index: indexSet){

            List<AnnotationInstance> instanceList = index.getAnnotations(DotName.createSimple(Frontier.class.getCanonicalName()));
            for(AnnotationInstance an: instanceList){

                Class fClazz = null;

                try {

                    fClazz = Class.forName(an.target().asClass().name().toString());
                    System.out.println("Frontier class detected : " + fClazz.getCanonicalName());
                    beansList.add(fClazz);

                }catch (Exception ex){

                    getServletContext().log("Error while attempting to register a frontier class",ex);
                    continue;

                }


            }



        }

        FrontierClass[] beanClasses = BeansCrawler.getInstance().crawl(beansList);


        System.out.println("Generating client-side code for frontiers...");
        Scripter scripter = new Scripter();

        for(FrontierClass beanClass : beanClasses){

            Frontiers.addFrontier(beanClass);
            String frontier_script = scripter.generate(beanClass);
            frontiersScript +="\n"+frontier_script;

        }


    }


    public void init() throws ServletException{

        if(initialized){

            System.out.println("---avoid double initialization");
            return;

        }

        initialized = true;

        System.out.println("---Initializing Hi-Framework servlet...");


        try{

            templateLoadListener = CDI.current().select(TemplateLoadListener.class).get();

        }catch (UnsatisfiedResolutionException ex){

            System.err.println("No implementation of the "+TemplateLoadListener.class.getSimpleName()+" interface found");

        }catch (Exception ex){

            getServletContext().log("Failed to lookup an implementation of the "+TemplateLoadListener.class.getSimpleName()+" interface",ex);
            ex.printStackTrace();

        }


        try{

            controllerCallsListener = CDI.current().select(ControllerCallsListener.class).get();

        }catch (UnsatisfiedResolutionException ex){

            System.err.println("No implementation of the "+ControllerCallsListener.class.getSimpleName()+" interface found");

        }catch (Exception ex){

            getServletContext().log("Failed to lookup an implementation of the "+ControllerCallsListener.class.getSimpleName()+" interface",ex);
            ex.printStackTrace();

        }


        try{

            frontierCallsListener = CDI.current().select(FrontierCallsListener.class).get();

        }catch (UnsatisfiedResolutionException ex){

            System.err.println("No implementation of the "+FrontierCallsListener.class.getSimpleName()+" interface found");

        }catch (Exception ex){

            getServletContext().log("Failed to lookup an implementation of the "+FrontierCallsListener.class.getSimpleName()+" interface",ex);
            ex.printStackTrace();

        }


        Set<Index> indexSet = BootstrapUtils.getIndexes(getServletContext());
        Set<Class<?>> configSections = new HashSet<>();

        if(indexSet!=null) {

            System.out.println("ConfigSection Class name : "+ConfigSection.class.getCanonicalName());

            for (Index index : indexSet) {

                List<AnnotationInstance> instances =
                        index.getAnnotations(DotName.createSimple(ConfigSection.class.getCanonicalName()));

                for (AnnotationInstance an : instances) {

                    String className = an.target().asClass().name().toString();
                    System.out.println("Loading config section class : "+className);

                    try {

                        Class<?> clazz  = this.getClass().getClassLoader().loadClass(className);
                        configSections.add(clazz);

                    } catch (Exception ex) {

                        getServletContext().log("Error initializing config section",ex);
                        continue;

                    }


                }

            }

        }

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.load(this.getServletContext(),configSections);




        readLibScript();
        readJavascriptInit();
        readLoaderScript();
        readGenericFrontier();
        findControllersAndMap();
        generateFrontiers();
        findAndLoadComponents();
        findTestedActions();
        initBootExtensions();


        if(AppConfigurations.get()!=null){

            ReqHandler.register(CDI.current().select(MVC.class).get(),MVC.class);
            ReqHandler.register(CDI.current().select(Assets.class).get(),Assets.class);
            ReqHandler.register(CDI.current().select(HiEcmaScript5.class).get(),HiEcmaScript5.class);
            ReqHandler.register(CDI.current().select(Frontiers.class).get(),Frontiers.class);
            ReqHandler.register(CDI.current().select(Tests.class).get(),Tests.class);
            ReqHandler.register(CDI.current().select(TestFiles.class).get(),TestFiles.class);
            HiEcmaScript5.prepareTemplates(this.getServletContext());

        }

        DEPLOY_ID = String.valueOf(new Date().getTime());

        System.out.println("---Finished Hi-Framework servlet initialization...");

    }



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        doHandle(request,response,true);

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doHandle(request,response,false);

    }


    private void findTestedAControllerActions(Index index){

        List<AnnotationInstance> instances = index.getAnnotations(DotName.createSimple(Tested.class.getCanonicalName()));
        for(AnnotationInstance an : instances){

            MethodInfo methodInfo =  an.target().asMethod();
            String actionURL = MVC.getActionMethodFromURLPart(methodInfo.name());


            String canonicalName = methodInfo.declaringClass().name().toString();
            String simpleName = canonicalName.substring(canonicalName.lastIndexOf('.')+1,canonicalName.length());
            String controllerURL = MVC.getURLController(simpleName);

            System.out.println("Tested controller action detected : "+controllerURL+"/"+actionURL);

            String testedViewPath = "/views/"+controllerURL+"/"+actionURL+".js";
            AppConfigurations.get().getTestedViews().put(testedViewPath,controllerURL+"/"+actionURL);
            String viewTestPath1 = "/webroot/tests/views/"+controllerURL+"/"+actionURL+"Test.js";

            AppConfigurations.get().getTestFiles().put(viewTestPath1,true);

        }

    }

    private void findTestedActions(){

        Set<Index> indexSet = BootstrapUtils.getIndexes(getServletContext());

        for(Index index : indexSet){

            findTestedAControllerActions(index);

        }

    }


    private void findControllersAndMap() throws HiException{

       Set<Index> indexSet = BootstrapUtils.getIndexes(getServletContext());
       if(indexSet==null)
            return;

       for(Index index : indexSet){

           Collection<ClassInfo> classInfos =
                   index.getAllKnownSubclasses(DotName.createSimple(Controller.class.getCanonicalName()));


           for(ClassInfo classInfo : classInfos){

               DotName dotName = classInfo.asClass().name();


               try {

                   System.out.println("Mapping controller class : "+dotName.toString());
                   Class controllerClazz = Class.forName(dotName.toString());

                   ControllersMapper.map(controllerClazz);

               }catch (ClassNotFoundException ex){

                   getServletContext().log("Error mapping controller class",ex);
                   continue;

               }

           }

       }

    }


    private void findAndLoadComponents() throws ServletException{

        Set<Index> indexSet = BootstrapUtils.getIndexes(getServletContext());
        if(indexSet==null)
            return;

        for(Index index : indexSet){

            List<AnnotationInstance> instances = index.
                    getAnnotations(DotName.createSimple(WebComponent.class.getCanonicalName()));

            for(AnnotationInstance an: instances){

                Class componentClass = null;

                try {

                    System.out.println("Loading web component : "+an.target().asClass().name().toString());
                    componentClass = Class.forName(an.target().asClass().toString());

                }catch (ClassNotFoundException ex){

                    continue;

                }

                String scriptName = componentClass.getSimpleName().toLowerCase();
                String minifiedScriptName = componentClass.getSimpleName().toLowerCase()+".min";

                URL componentScript =  null;

                try {

                    if (AppConfigurations.get().underDevelopment())
                        componentScript = getServletContext().getResource("/" + scriptName + ".js");
                    else
                        componentScript = getServletContext().getResource("/" + minifiedScriptName + ".js");

                }catch (MalformedURLException ex){

                    throw new HiException("Invalid component name : "+componentClass.getSimpleName(),ex);

                }

                if(componentScript==null)
                    continue;

                try {

                    componentsScript+=Helper.readTextStreamToEnd(componentScript.openStream(), null);
                    getServletContext().log(componentClass.getSimpleName()+" Web component loaded");

                }catch (Exception ex){

                    throw new HiException("Could not read component script : "+componentClass.getSimpleName(),ex);

                }


            }


        }


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
