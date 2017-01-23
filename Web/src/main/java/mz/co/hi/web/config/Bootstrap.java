package mz.co.hi.web.config;

import com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory;
import mz.co.hi.web.exceptions.HiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Mario Junior.
 */
public final class Bootstrap {


    public static String LOGGER=null;
    private static Logger _log=null;

    public static void setLogger(String name){

        LOGGER = name;
        _log = LoggerFactory.getLogger(Bootstrap.LOGGER);

    }

    private Configurator getConfigurator(Class<? extends Configurator> clazz) throws HiException {

        try{

            Constructor constructor = clazz.getDeclaredConstructor(null);
            Object configurator = constructor.newInstance();
            return (Configurator) configurator;

        }catch (Exception ex){

            throw new BadConfiguratorException("Failed to initialize the configurator "+clazz.getCanonicalName(),ex);

        }

    }

    public void load(ServletContext servletContext,Set<Class<?>> configuratorClasses) throws HiException{

        Document document = loadDocument(servletContext);
        Element docElement = document.getDocumentElement();

        for(Class clazz : configuratorClasses){

            if(clazz.asSubclass(Configurator.class)==null)
                throw new BadConfiguratorException("Class "+clazz.getCanonicalName()+" does not implement the Configurator interface");

            Configurator configurator = getConfigurator(clazz);
            ConfigSection section = (ConfigSection) clazz.getDeclaredAnnotation(ConfigSection.class);

            HashMap<String,Element> elements = new HashMap<>();

            for(String tag: section.tags()){

                NodeList nodeList = docElement.getElementsByTagName(tag);
                if(nodeList.getLength()==0)
                    continue;

                elements.put(tag,(Element) nodeList.item(0));

            }

            if(elements.size()==0) {
                _log.info("Configurator " + clazz.getCanonicalName() + " was skipped. No match for the defined tags");
                continue;
            }


            _log.info("Loading configurator " + clazz.getCanonicalName() + "...");
            configurator.doConfig(AppConfigurations.get(),elements,docElement);

        }

    }


    private Document loadDocument(ServletContext servletContext) throws HiException{

        org.w3c.dom.Document document = null;

        URL xsdPath = null;

        try {

            xsdPath = servletContext.getResource("/hi-config.xsd");

        }catch (Exception ex){



        }

        if(xsdPath==null){

            throw new BadConfigException("Hi XML namespace XSD file could not be loaded. Make sure the Hi Resources library is in your classpath.");

        }


        URL hiXML = null;

        try {

            hiXML = servletContext.getResource("/WEB-INF/hi.xml");


        }catch (Exception ex){

            throw new BadConfigException("Hi configuration file could not be found in path /WEB-INF/hi.xml. Application can't start successfully without this file");

        }


        InputStream xml = null;

        try {

            xml = hiXML.openStream();

        }catch (Exception ex){

            throw new BadConfigException("Failed reading Hi configuration file in path /WEB-INF/hi.xml");

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

            throw new BadConfigException("Invalid Hi configuration file. The file is not formatted as expected. Check the documentation",ex);

        }

        return document;

    }

}
