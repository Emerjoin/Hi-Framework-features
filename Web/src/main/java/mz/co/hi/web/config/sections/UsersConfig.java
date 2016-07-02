package mz.co.hi.web.config.sections;

import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.config.BadConfigException;
import mz.co.hi.web.config.ConfigSection;
import mz.co.hi.web.config.Configurator;
import mz.co.hi.web.users.UDetailsProvider;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.enterprise.inject.spi.CDI;
import java.util.Map;

/**
 * @author Mário Júnior
 */
@ConfigSection(tags = "users")
public class UsersConfig implements Configurator {

    @Override
    public void doConfig(AppConfigurations configs, Map<String, Element> elements, Element document) throws BadConfigException {

        Element usersConfigElement = elements.get("users");
        NodeList userDetailsProviderList = usersConfigElement.getElementsByTagName("details-provider-class");

        if(userDetailsProviderList.getLength()>0) {

                Element userDetailsProvider = (Element) userDetailsProviderList.item(0);

                if (userDetailsProvider.getTextContent().trim().equals("")) {

                    throw new BadConfigException("Empty User details provider class name supplied.");

                }

                String userDetailsProviderClassName = userDetailsProvider.getTextContent();
                Class userDetailsProviderClass = null;

                try {

                    userDetailsProviderClass = Class.forName(userDetailsProviderClassName);

                } catch (ClassNotFoundException ex) {

                    throw new BadConfigException("User details provider class could not be found", ex);

                }

                if (userDetailsProviderClass.asSubclass(UDetailsProvider.class) == null)
                    throw new BadConfigException("The provided User details provider does not implement the expected interface");


                Object userDetailsProviderInstance = null;


                try {

                    userDetailsProviderInstance = CDI.current().select(userDetailsProviderClass).get();
                    UDetailsProvider detailsProvider = (UDetailsProvider) userDetailsProviderInstance;
                    configs.setUserDetailsProvider(detailsProvider);

                }catch (Exception ex){

                    //CDI Instantiation failed

                    try {

                        userDetailsProviderInstance = userDetailsProviderClass.newInstance();
                        UDetailsProvider detailsProvider = (UDetailsProvider) userDetailsProviderInstance;
                        configs.setUserDetailsProvider(detailsProvider);

                    } catch (Exception exx) {

                        throw new BadConfigException("User details provider instantiation failed", exx);

                    }


                }


        }


    }


}
