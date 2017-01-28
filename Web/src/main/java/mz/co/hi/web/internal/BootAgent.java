package mz.co.hi.web.internal;

import mz.co.hi.web.BootstrapUtils;
import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.ConfigSection;
import mz.co.hi.web.config.ConfigurationsAgent;
import mz.co.hi.web.exceptions.HiException;
import mz.co.hi.web.extension.BootExtension;
import mz.co.hi.web.extension.BootManager;
import mz.co.hi.web.meta.Tested;
import mz.co.hi.web.mvc.Controller;
import mz.co.hi.web.mvc.ControllersMapper;
import mz.co.hi.web.req.MVC;
import org.jboss.jandex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for performing initialization of a Hi-Application upon deployment.
 * From loading the configuration file to the generation of frontiers code.
 * @author Mário Júnior
 */
@ApplicationScoped
public class BootAgent {

    //TODO: Set deployId
    //TODO: Call BootStrap to load configurations
    //TODO: Call ES5Library to load scripts and generate frontiers
    //TODO: Call HiRouter and register all the available requests handlers
    //TODO: Find controller and map
    //TODO: Find frontier and map
    //TODO: Find tested actions
    //TODO: Initilialize Boot extensions
    //TODO: Set global listeners


    private ServletContext servletContext = null;
    private ServletConfig servletConfig = null;
    private String loggerName = null;
    private Logger _log = null;
    private String deployId ="";

    @Inject
    private ES5Library scriptLibrary;

    @Inject
    private Router router;


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

        for(Index index : indexSet){

            $findTestedAControllerActions(index);

        }

    }

    private void $findTestedAControllerActions(Index index){

        List<AnnotationInstance> instances = index.getAnnotations(DotName.createSimple(Tested.class.getCanonicalName()));
        for(AnnotationInstance an : instances){

            MethodInfo methodInfo =  an.target().asMethod();
            String actionURL = MVC.getActionMethodFromURLPart(methodInfo.name());


            String canonicalName = methodInfo.declaringClass().name().toString();
            String simpleName = canonicalName.substring(canonicalName.lastIndexOf('.')+1,canonicalName.length());
            String controllerURL = MVC.getURLController(simpleName);

            _log.info("Tested controller action detected : "+controllerURL+"/"+actionURL);

            String testedViewPath = "/views/"+controllerURL+"/"+actionURL+".js";
            AppConfigurations.get().getTestedViews().put(testedViewPath,controllerURL+"/"+actionURL);
            String viewTestPath1 = "/webroot/tests/views/"+controllerURL+"/"+actionURL+"Test.js";

            AppConfigurations.get().getTestFiles().put(viewTestPath1,true);

        }

    }

    private void loadConfigs() throws HiException{

        //TODO: Abstract the configurations provider : Can be an XML file or anything else (this will ease unit testing: no xml required)
        Set<Index> indexSet = BootstrapUtils.getIndexes(servletContext);
        Set<Class<?>> configSections = new HashSet<>();

        if(indexSet!=null) {

            for (Index index : indexSet) {

                List<AnnotationInstance> instances =
                        index.getAnnotations(DotName.createSimple(ConfigSection.class.getCanonicalName()));

                for (AnnotationInstance an : instances) {

                    String className = an.target().asClass().name().toString();
                    _log.info(String.format("Loading config section class : %s",className));

                    try {

                        Class<?> clazz  = this.getClass().getClassLoader().loadClass(className);
                        configSections.add(clazz);

                    } catch (Throwable ex) {

                        _log.error("Failed to load config section",ex);
                        continue;

                    }
                }
            }
        }

        ConfigurationsAgent configurationsAgent = new ConfigurationsAgent();
        configurationsAgent.load(servletContext,configSections);
    }


    private void makeDeployId(){

        //TODO: Generate a Deployment Id

    }



    public void init(ServletContext context, ServletConfig config) throws HiException{

        this.servletContext = null;
        this.servletConfig = config;

        //TODO: Set logger name based in servletConfig
        _log = LoggerFactory.getLogger(loggerName);
        makeDeployId(); //Set deploy Id
        loadConfigs(); //Load App configurations
        findControllersAndMap(); //Find all the controller and map them
        findTestedActions(); //Find all the tested controllers actions //TODO: Make this bypassable via servlet config
        scriptLibrary.init(servletContext);//Load scripts and generate frontiers
        router.init(servletContext,servletConfig); //Register requests handlers
        initBootExtensions(); //Load and execute boot extensions

    }



    public String getLoggerName(){

        //TODO: Implement
        return null;

    }

    public String getDeployId(){

        return deployId;

    }


}
