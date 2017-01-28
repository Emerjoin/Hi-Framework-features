package mz.co.hi.web.internal;

import mz.co.hi.web.BootstrapUtils;
import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.ConfigProvider;
import mz.co.hi.web.exceptions.HiException;
import mz.co.hi.web.extension.BootExtension;
import mz.co.hi.web.extension.BootManager;
import mz.co.hi.web.meta.Tested;
import mz.co.hi.web.mvc.Controller;
import mz.co.hi.web.mvc.ControllersMapper;
import mz.co.hi.web.req.MVCReqHandler;
import org.jboss.jandex.*;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.*;

/**
 * This class is responsible for performing initialization of a Hi-Application upon deployment.
 * @author Mário Júnior
 */
@ApplicationScoped
public class BootAgent {

    private ServletContext servletContext = null;
    private ServletConfig servletConfig = null;
    private Logger _log = null;
    private String deployId ="";

    @Inject
    private ES5Library scriptLibrary;

    @Inject
    private Router router;

    @Inject
    private ConfigProvider configProvider;


    public BootAgent(){}

    private void initBootExtensions() throws HiException {

        Set<Index> indexSet = BootstrapUtils.getIndexes(servletContext);
        if(indexSet==null)
            return;

        Iterable<BootExtension> bootExtensions = BootManager.getExtensions();
        _log.info("Initializing boot extensions...");

        for(BootExtension extension : bootExtensions) {

            try {

                extension.boot(indexSet);
                _log.info(String.format("Initializing boot extension : %s",extension.getClass().getCanonicalName()));

            }catch (Exception ex){

                throw new HiException("Failed to initialize boot extension : "+extension.getClass().getCanonicalName(),ex);

            }

        }

        _log.info("Finalized initialization of boot extensions.");

    }



    private void findControllersAndMap() throws HiException{

        Set<Index> indexSet = BootstrapUtils.getIndexes(servletContext);
        if(indexSet==null)
            return;

        for(Index index : indexSet){

            Collection<ClassInfo> classInfos =
                    index.getAllKnownSubclasses(DotName.createSimple(Controller.class.getCanonicalName()));


            for(ClassInfo classInfo : classInfos){

                DotName dotName = classInfo.asClass().name();


                try {

                    _log.info("Mapping controller class : "+dotName.toString());
                    Class controllerClazz = Class.forName(dotName.toString());

                    ControllersMapper.map(controllerClazz);

                }catch (ClassNotFoundException ex){

                    _log.error("Error mapping controller class",ex);
                    continue;

                }

            }

        }

    }


    private void findTestedActions(){

        Set<Index> indexSet = BootstrapUtils.getIndexes(servletContext);

        for(Index index : indexSet)
            findTestedAControllerActions(index);


    }

    private void findTestedAControllerActions(Index index){

        List<AnnotationInstance> instances = index.getAnnotations(DotName.createSimple(Tested.class.getCanonicalName()));
        for(AnnotationInstance an : instances){

            MethodInfo methodInfo =  an.target().asMethod();
            String actionURL = MVCReqHandler.getActionMethodFromURLPart(methodInfo.name());


            String canonicalName = methodInfo.declaringClass().name().toString();
            String simpleName = canonicalName.substring(canonicalName.lastIndexOf('.')+1,canonicalName.length());
            String controllerURL = MVCReqHandler.getURLController(simpleName);

            _log.info("Tested controller action detected : "+controllerURL+"/"+actionURL);

            String testedViewPath = "/views/"+controllerURL+"/"+actionURL+".js";
            AppConfigurations.get().getTestedViews().put(testedViewPath,controllerURL+"/"+actionURL);
            String viewTestPath1 = "/webroot/tests/views/"+controllerURL+"/"+actionURL+"Test.js";

            AppConfigurations.get().getTestFiles().put(viewTestPath1,true);

        }

    }

    private void loadConfigs() throws HiException{

        Set<Index> indexSet = BootstrapUtils.getIndexes(servletContext);
        configProvider.load(servletContext,servletConfig,indexSet);
        _log = configProvider.getLogger();

    }


    private void makeDeployId(){

        deployId = String.valueOf(Calendar.getInstance().getTimeInMillis());

    }



    public void init(ServletContext context, ServletConfig config) throws HiException{

        this.servletContext = context;
        this.servletConfig = config;

        makeDeployId(); //Set deploy Id
        loadConfigs(); //Load App configurations
        findControllersAndMap(); //Find all the controller and map them
        findTestedActions(); //Find all the tested controllers actions
        scriptLibrary.init(servletContext);//Load scripts and generate frontiers
        router.init(servletContext,servletConfig); //Register requests handlers
        initBootExtensions(); //Load and execute boot extensions

    }


    public String getDeployId(){

        return deployId;

    }


}
