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
@ConfigSection(tags = "web-tunning")
public class WebTunningConfig implements Configurator{

    @Override
    public void doConfig(AppConfigurations configs, Map<String, Element> elements, Element document) throws BadConfigException {

            Element tunningElement = elements.get("web-tunning");
            NodeList webrootNodeList = tunningElement.getElementsByTagName("webroot");


            Element webrootElement = null;

            if(webrootNodeList.getLength()>0)
                webrootElement = (Element) webrootNodeList.item(0);
            else
                return;


            NodeList staticFoldersCachingNodesList = webrootElement.getElementsByTagName("folders-fixed-caching");

            if(staticFoldersCachingNodesList.getLength()>0){

                Element staticFoldersCachingElement = (Element) staticFoldersCachingNodesList.item(0);
                readStaticFoldersCachingConfigs(staticFoldersCachingElement,configs);

            }

            NodeList smartAssetsCachingNodesList = webrootElement.getElementsByTagName("assets-smart-caching");
            if(smartAssetsCachingNodesList.getLength()>0){

                Element smartAssetsCachingElement = (Element) smartAssetsCachingNodesList.item(0);
                readSmartTunningConfigs(smartAssetsCachingElement,configs);

            }


    }

    private void readStaticFoldersCachingConfigs(Element staticFoldersCachingElement, AppConfigurations appConfigurations){

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

    private void readSmartTunningConfigs(Element smartAssetsCachingElement, AppConfigurations appConfigurations){

        NodeList smartCachedNodeList =  smartAssetsCachingElement.getElementsByTagName("enabled");
        if(smartCachedNodeList.getLength()==0)
            return;

        for(int i=0;i<smartCachedNodeList.getLength();i++){

            Element element = (Element) smartCachedNodeList.item(i);
            String assetURI = element.getTextContent();
            appConfigurations.getTunnings().enableSmartCaching(assetURI);

        }

    }


}
