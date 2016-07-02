package mz.co.hi.web.config.sections;

import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.BadConfigException;
import mz.co.hi.web.config.ConfigSection;
import mz.co.hi.web.config.Configurator;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * @author Mário Júnior
 */
@ConfigSection(tags = "web")
public class WebConfig implements Configurator {

    @Override
    public void doConfig(AppConfigurations configs, Map<String, org.w3c.dom.Element> elements, org.w3c.dom.Element docElement) throws BadConfigException {

        //Template and MVC Configurations
        org.w3c.dom.Element webElement = (org.w3c.dom.Element) docElement.getElementsByTagName("web").item(0);

        org.w3c.dom.Element controllersPackageElement = (org.w3c.dom.Element) webElement.getElementsByTagName("controllers-package").item(0);
        org.w3c.dom.Element viewsDirectoryElement =(org.w3c.dom.Element) webElement.getElementsByTagName("views-directory").item(0);
        org.w3c.dom.Element welcomeUrlElement =(org.w3c.dom.Element) webElement.getElementsByTagName("welcome-url").item(0);


        String controllersPackage = controllersPackageElement.getTextContent();
        String viewsDirectory = viewsDirectoryElement.getTextContent();
        String wecomeUrl = welcomeUrlElement.getTextContent();

        org.w3c.dom.Element templateElement = (org.w3c.dom.Element) webElement.getElementsByTagName("templates").item(0);
        NodeList templatesList = templateElement.getElementsByTagName("template");

        String[] templates = new String[templatesList.getLength()];

        for(int i=0;i<templatesList.getLength();i++){

            templates[i] = templatesList.item(i).getTextContent();

        }

        configs.setControllersPackageName(controllersPackage);
        configs.setViewsDirectory(viewsDirectory);
        configs.setTemplates(templates);
        configs.setWelcomeUrl(wecomeUrl);


        NodeList deploymentModeNodeList = docElement.getElementsByTagName("deployment-mode");
        if(deploymentModeNodeList.getLength()>0){


            String deploymentModevalue = deploymentModeNodeList.item(0).getTextContent();
            if(deploymentModevalue.equals("PRODUCTION"))
                configs.setDeploymentMode(AppConfigurations.DeploymentMode.PRODUCTION);



        }

        NodeList defaultLangNode = docElement.getElementsByTagName("default-lang");
        if(defaultLangNode.getLength()>0){

            org.w3c.dom.Element defaultLangElement = (org.w3c.dom.Element) defaultLangNode.item(0);
            configs.setDefaultLanguage(defaultLangElement.getTextContent());


        }



    }




}
