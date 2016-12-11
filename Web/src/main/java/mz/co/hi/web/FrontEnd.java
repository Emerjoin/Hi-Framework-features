package mz.co.hi.web;

import mz.co.hi.web.config.AppConfigurations;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class FrontEnd {

    @Inject
    private HttpServletRequest httpServletRequest;

    @Inject
    private ActiveUser activeUser;

    private String template;
    private String language;

    private Map<String,Map> templateData = new HashMap<>();

    public static String TEMPLATE_SESSION_VARIABLE ="$template_";
    public static String LANGUAGE_SESSION_VARIABLE="$language_";

    private Map<String,Map> laterInvocations = new HashMap<>();

    public FrontEnd(){



    }


    @PostConstruct
    private void onReady(){

        this.language = AppConfigurations.get().getDefaultLanguage();

        Object templateObject = activeUser.getProperty(TEMPLATE_SESSION_VARIABLE);
        if(templateObject==null){

            template="index";
            activeUser.setProperty(TEMPLATE_SESSION_VARIABLE,template);

        }else template = templateObject.toString();


        Object langObject = activeUser.getProperty(LANGUAGE_SESSION_VARIABLE);
        if(langObject==null){

            language = AppConfigurations.get().getDefaultLanguage();
            activeUser.setProperty(LANGUAGE_SESSION_VARIABLE,language);

        }else language = langObject.toString();


    }



    public String getLanguage() {
        return language;
    }


    public String getTemplate() {

        return template;

    }


    public String getLangDictionary(){

        String dictPath = "/i18n/"+language+".json";
        String dict = "{}";

        try {


            URL resource = httpServletRequest.getServletContext().getResource(dictPath);
            if(resource==null)
                return dict;


            dict = Helper.readTextStreamToEnd(resource.openStream(),null);
            return dict;

        }catch (Exception ex){

            return dict;

        }

    }


    public void refresh(){

        invokeAfter("reload", Collections.emptyMap());

    }

    public void refresh(String url){

        Map<String,Object> map = new HashMap<>();
        map.put("url",url);

        invokeAfter("reload", map);

    }




    public void invokeAfter(String actionName, Map params) {

        laterInvocations.put("$"+actionName,params);

    }

    public void ajaxRedirect(String url){

        Map map = new HashMap<>();
        map.put("url",url);
        invokeAfter("redirect",map);

    }


    public boolean gotLaterInvocations() {

        return laterInvocations.size()>0;

    }

    public boolean isRequestAjax(){

        return "XMLHttpRequest".equals(httpServletRequest.getHeader("X-Requested-With"));

    }

    public boolean isFrontierRequest(){

        return httpServletRequest.getRequestURL().indexOf("f.m.call")!=-1;

    }


    public Map<String,Map> getLaterInvocations() {
        return laterInvocations;
    }

    public Map getTemplateData() {
        return templateData;
    }

    public void setTemplateData(Map templateData) {
        this.templateData = templateData;
    }

    public void setLanguage(String name){

        this.language = name;

        activeUser.setProperty(LANGUAGE_SESSION_VARIABLE,this.language);

        //Set reload command if this method is invoked on an ajax request
        if(isRequestAjax()||isFrontierRequest())
            refresh();



    }

    public void setTemplate(String template) {

        String currentTemplate = activeUser.getProperty(TEMPLATE_SESSION_VARIABLE).toString();

        //Same template
        if(template.equals(currentTemplate)) {

            System.out.println("Trying to set the same template name : "+template);
            return;

        }

        System.out.println("Changing template "+currentTemplate+" => "+template);

        this.template = template;

        activeUser.setProperty(TEMPLATE_SESSION_VARIABLE,this.template);

        //Reload command if this method is invoked on an ajax request
        if(isRequestAjax()||isFrontierRequest())
            refresh(httpServletRequest.getRequestURI());


    }

    public boolean wasTemplateDataSet(){

        return templateData.size()>0;

    }




}
