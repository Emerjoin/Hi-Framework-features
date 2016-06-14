package mz.co.hi.web.config;

import com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory;
import mz.co.hi.web.users.UDetailsProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Mario Junior.
 */
public class ConfigLoader {


    public static void load(ServletContext servletContext) throws ServletException{

        Document document = loadDocument(servletContext);
        Element docElement = document.getDocumentElement();


        //Template and MVC Configurations
        Element webElement = (Element) docElement.getElementsByTagName("web").item(0);

        Element controllersPackageElement = (Element) webElement.getElementsByTagName("controllers-package").item(0);
        Element viewsDirectoryElement =(Element) webElement.getElementsByTagName("views-directory").item(0);
        Element welcomeUrlElement =(Element) webElement.getElementsByTagName("welcome-url").item(0);



        String controllersPackage = controllersPackageElement.getTextContent();
        String viewsDirectory = viewsDirectoryElement.getTextContent();
        String wecomeUrl = welcomeUrlElement.getTextContent();


        Element templateElement = (Element) webElement.getElementsByTagName("templates").item(0);
        NodeList templatesList = templateElement.getElementsByTagName("template");

        String[] templates = new String[templatesList.getLength()];

        for(int i=0;i<templatesList.getLength();i++){

            templates[i] = templatesList.item(i).getTextContent();

        }

        AppConfigurations appConfigurations = new AppConfigurations(controllersPackage,viewsDirectory,templates);
        appConfigurations.setWelcomeUrl(wecomeUrl);


        NodeList deploymentModeNodeList = docElement.getElementsByTagName("deployment-mode");
        if(deploymentModeNodeList.getLength()>0){


            String deploymentModevalue = deploymentModeNodeList.item(0).getTextContent();
            if(deploymentModevalue.equals("PRODUCTION"))
                appConfigurations.setDeploymentMode(AppConfigurations.DeploymentMode.PRODUCTION);



        }



        NodeList defaultLangNode = docElement.getElementsByTagName("default-lang");
        if(defaultLangNode.getLength()>0){

            Element defaultLangElement = (Element) defaultLangNode.item(0);
            appConfigurations.setDefaultLanguage(defaultLangElement.getTextContent());


        }


        //Users setting
        NodeList usersConfigNodeList = docElement.getElementsByTagName("users");
        if(usersConfigNodeList.getLength()>0){

            //Details provider
            Element usersConfigElement = (Element) usersConfigNodeList.item(0);
            NodeList userDetailsProviderList = usersConfigElement.getElementsByTagName("details-provider-class");
            if(userDetailsProviderList.getLength()>0) {


                Element userDetailsProvider = (Element) userDetailsProviderList.item(0);
                if (userDetailsProvider.getTextContent().trim().equals("")) {

                    throw new ServletException("Empty User details provider class name supplied.");

                }

                String userDetailsProviderClassName = userDetailsProvider.getTextContent();
                Class userDetailsProviderClass = null;

                try {

                    userDetailsProviderClass = Class.forName(userDetailsProviderClassName);

                } catch (ClassNotFoundException ex) {

                    throw new ServletException("User details provider class could not be found", ex);

                }

                if (userDetailsProviderClass.asSubclass(UDetailsProvider.class) == null)
                    throw new ServletException("The provided User details provider does not implement the expected interface");


                Object userDetailsProviderInstance = null;


                try {

                    userDetailsProviderInstance = CDI.current().select(userDetailsProviderClass).get();
                    UDetailsProvider detailsProvider = (UDetailsProvider) userDetailsProviderInstance;
                    appConfigurations.setUserDetailsProvider(detailsProvider);

                }catch (Exception ex){

                    //CDI Instantiation failed

                    try {

                        userDetailsProviderInstance = userDetailsProviderClass.newInstance();
                        UDetailsProvider detailsProvider = (UDetailsProvider) userDetailsProviderInstance;
                        appConfigurations.setUserDetailsProvider(detailsProvider);

                    } catch (Exception exx) {

                        throw new ServletException("User details provider instantiation failed", exx);

                    }


                }


            }

        }

        //Frontiers configurations
        NodeList frontiersConfigNodeList = docElement.getElementsByTagName("frontiers");
        if(frontiersConfigNodeList.getLength()>0){

            Element frontiersElement = (Element) frontiersConfigNodeList.item(0);
            NodeList allFrontiersNodeList = frontiersElement.getElementsByTagName("frontier");

            if(allFrontiersNodeList.getLength()>0){

                for(int i=0;i<allFrontiersNodeList.getLength();i++){

                    Element frontierElement = (Element) allFrontiersNodeList.item(i);
                    appConfigurations.getFrontiers().add(frontierElement.getTextContent());


                }

            }


        }


        readTunningConfigs(docElement,appConfigurations);
        readTestConfigs(docElement,appConfigurations);
        AppConfigurations.set(appConfigurations);

    }



    private static void readTestConfigs(Element docElement, AppConfigurations appConfigurations){

        NodeList testsNodeList = docElement.getElementsByTagName("tests");
        if(testsNodeList.getLength()<1)
            return;


        Element testsElement = (Element) testsNodeList.item(0);
        NodeList testNodes = testsElement.getElementsByTagName("script");

        if(testNodes.getLength()<1)
            return;

        for(int i=0;i<testNodes.getLength();i++){

            Element element = (Element) testNodes.item(i);
            appConfigurations.getTestFiles().put(element.getTextContent(),true);

        }



    }


    private static void readStaticFoldersCachingConfigs(Element staticFoldersCachingElement, AppConfigurations appConfigurations){

        NodeList foldersHttpCacheList = staticFoldersCachingElement.getElementsByTagName("enabled");

        if(foldersHttpCacheList.getLength()==0)
            return;


        for(int i=0; i<foldersHttpCacheList.getLength();i++){

            Element folderHttpCache = (Element) foldersHttpCacheList.item(i);
            int age = Integer.parseInt(folderHttpCache.getAttribute("age"));
            String ageUnit = folderHttpCache.getAttribute("age-unit");
            String folderName = folderHttpCache.getAttribute("folder-name");


            long hour_milliseconds = 3600000;
            long day_milliseconds = hour_milliseconds*24;
            long week_milliseconds = day_milliseconds*7;
            long month_milliseconds = week_milliseconds*4;
            long year_milliseconds = month_milliseconds*12;



            long millis_times_factor = 0;

            switch (ageUnit){

                case "HOURS":   millis_times_factor = hour_milliseconds;
                    break;

                case "DAYS":    millis_times_factor = day_milliseconds;
                    break;

                case "WEEKS":   millis_times_factor = week_milliseconds;
                    break;


                case  "MONTHS": millis_times_factor = month_milliseconds;
                    break;

                case  "YEARS" : millis_times_factor = year_milliseconds;
                    break;

                default: millis_times_factor = 0;

            }

            long cache_time = millis_times_factor*age;
            if(cache_time==0)
                continue;

            appConfigurations.getTunnings().setCachedDirectory(folderName,cache_time);


        }

    }

    private static void readSmartTunningConfigs(Element smartAssetsCachingElement, AppConfigurations appConfigurations){

        NodeList smartCachedNodeList =  smartAssetsCachingElement.getElementsByTagName("enabled");
        if(smartCachedNodeList.getLength()==0)
            return;

        for(int i=0;i<smartCachedNodeList.getLength();i++){

            Element element = (Element) smartCachedNodeList.item(i);
            String assetURI = element.getTextContent();
            appConfigurations.getTunnings().enableSmartCaching(assetURI);

        }

    }

    private static void readTunningConfigs(Element docElement, AppConfigurations appConfigurations){

        NodeList tunningConfigNodeList = docElement.getElementsByTagName("web-tunning");
        if(tunningConfigNodeList.getLength()>0){

            Element tunningElement = (Element) tunningConfigNodeList.item(0);
            NodeList webrootNodeList = tunningElement.getElementsByTagName("webroot");


            Element webrootElement = null;

            if(webrootNodeList.getLength()>0)
                webrootElement = (Element) webrootNodeList.item(0);
            else
                return;


            NodeList staticFoldersCachingNodesList = webrootElement.getElementsByTagName("folders-fixed-caching");

            if(staticFoldersCachingNodesList.getLength()>0){

                Element staticFoldersCachingElement = (Element) staticFoldersCachingNodesList.item(0);
                readStaticFoldersCachingConfigs(staticFoldersCachingElement,appConfigurations);

            }

            NodeList smartAssetsCachingNodesList = webrootElement.getElementsByTagName("assets-smart-caching");
            if(smartAssetsCachingNodesList.getLength()>0){

                Element smartAssetsCachingElement = (Element) smartAssetsCachingNodesList.item(0);
                readSmartTunningConfigs(smartAssetsCachingElement,appConfigurations);

            }


        }



    }

    private static Document loadDocument(ServletContext servletContext) throws ServletException{

        org.w3c.dom.Document document = null;

        URL xsdPath = null;

        try {

            xsdPath = servletContext.getResource("/hi-config.xsd");

        }catch (Exception ex){



        }

        if(xsdPath==null){

            throw new ServletException("Hi XML namespace XSD file could not be loaded. Make sure the Hi Resources library is in your classpath.");

        }


        URL yayeeXml = null;

        try {

            yayeeXml = servletContext.getResource("/WEB-INF/hi.xml");


        }catch (Exception ex){

            throw new ServletException("Hi configuration file could not be found in path /WEB-INF/hi.xml. Application can't start successfully without this file");

        }


        InputStream xml = null;

        try {

            xml = yayeeXml.openStream();

        }catch (Exception ex){

            throw new ServletException("Failed reading Hi configuration file in path /WEB-INF/hi.xml");

        }

        try {


            byte[] buffer = new byte[xml.available()];
            xml.read(buffer);

            ByteArrayInputStream byteArray1 = new ByteArrayInputStream(buffer);
            ByteArrayInputStream byteArray2 = new ByteArrayInputStream(buffer);


            XMLSchemaFactory factory =
                    new XMLSchemaFactory();
            Source schemaFile = new StreamSource(xsdPath.openStream());
            Source xmlSource = new StreamSource(byteArray1);
            Schema schema = factory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlSource);

            document = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(byteArray2);

        }catch (Exception ex){

            throw new ServletException("Invalid Hi configuration file. The file is not formatted as expected. Check the documentation",ex);

        }

        return document;

    }

}
