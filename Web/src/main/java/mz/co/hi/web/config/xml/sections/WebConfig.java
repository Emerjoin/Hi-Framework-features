package mz.co.hi.web.config.xml.sections;

import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.BadConfigException;
import mz.co.hi.web.config.Configurator;
import mz.co.hi.web.config.xml.ConfigSection;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * @author Mário Júnior
 */
@ConfigSection(tags = "web")
public class WebConfig implements Configurator {

    public static final String DEFAULT_VIEWS_DIRECTORY = "views";


    private void templates(Element webElement, AppConfigurations configs){
        NodeList templatesNodes = webElement.getElementsByTagName("templates");
        String[] templates = null;

        if(templatesNodes.getLength()>0){

            org.w3c.dom.Element templateElement = (Element) templatesNodes.item(0);
            NodeList templatesList = templateElement.getElementsByTagName("template");

            templates = new String[templatesList.getLength()];

            for(int i=0;i<templatesList.getLength();i++){

                templates[i] = templatesList.item(i).getTextContent();

            }

        }else templates=new String[]{"index"};
        configs.setTemplates(templates);

    }


    private void deploymentAndLang(Element docElement, AppConfigurations configs){
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

    @Override
    public void doConfig(AppConfigurations configs, Map<String, org.w3c.dom.Element> elements, org.w3c.dom.Element docElement) throws BadConfigException {

        //Template and MVC Configurations
        org.w3c.dom.Element webElement = (org.w3c.dom.Element) docElement.getElementsByTagName("web").item(0);
        NodeList viewsDirectoryNodes = webElement.getElementsByTagName("views-directory");
        if(viewsDirectoryNodes.getLength()>0){
            org.w3c.dom.Element viewsDirectoryElement =(org.w3c.dom.Element) viewsDirectoryNodes.item(0);
            String viewsDirectory = viewsDirectoryElement.getTextContent();
            configs.setViewsDirectory(viewsDirectory);
        }else configs.setViewsDirectory(DEFAULT_VIEWS_DIRECTORY);

        NodeList welcomeUrlNodes = webElement.getElementsByTagName("welcome-url");
        if(welcomeUrlNodes.getLength()>0){
            org.w3c.dom.Element welcomeUrlElement =(org.w3c.dom.Element) welcomeUrlNodes.item(0);
            String wecomeUrl = welcomeUrlElement.getTextContent();
            configs.setWelcomeUrl(wecomeUrl);
        }


       templates(webElement,configs);
       deploymentAndLang(docElement,configs);

    }





}
