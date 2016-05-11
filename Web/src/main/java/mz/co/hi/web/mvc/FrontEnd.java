package mz.co.hi.web.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class FrontEnd {

    private Map actionsToPerform = null;
    private String template = "index.html";
    private String language = "default";
    private Map templateData = new HashMap<>();


    public void setLanguage(String name){

        language = name;

    }

    public String getLanguage() {
        return language;
    }

    public void perform(String actionName, Map data){



    }

    public String getTemplate() {

        return template;

    }

    public void setTemplate(String template) {

        this.template = template;

    }

    public Map getTemplateData() {
        return templateData;
    }

    public void setTemplateData(Map templateData) {
        this.templateData = templateData;
    }
}
