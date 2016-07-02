package mz.co.hi.web.config.sections;

import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.BadConfigException;
import mz.co.hi.web.config.ConfigSection;
import mz.co.hi.web.config.Configurator;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * @author Mário Júnior
 */
@ConfigSection(tags = "frontier-packages")
public class FrontiersConfig implements Configurator {


    @Override
    public void doConfig(AppConfigurations configs, Map<String, Element> elements, Element document) throws BadConfigException {

        Element frontiersElement = elements.get("frontier-packages");
        NodeList allFrontiersNodeList = frontiersElement.getElementsByTagName("package");

        if(allFrontiersNodeList.getLength()>0){

            for(int i=0;i<allFrontiersNodeList.getLength();i++){

                Element frontierElement = (Element) allFrontiersNodeList.item(i);
                configs.getFrontierPackages().add(frontierElement.getTextContent());

            }

        }


    }

}
