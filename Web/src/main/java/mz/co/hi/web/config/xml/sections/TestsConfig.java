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
@ConfigSection(tags = "tests")
public class TestsConfig implements Configurator {


    @Override
    public void doConfig(AppConfigurations configs, Map<String, Element> elements, Element document) throws BadConfigException {

        NodeList testNodes = elements.get("tests").getElementsByTagName("script");
        if(testNodes.getLength()<1)
            return;

        for(int i=0;i<testNodes.getLength();i++){

            Element element = (Element) testNodes.item(i);
            configs.getTestFiles().put(element.getTextContent(),true);

        }

    }


}
