package mz.co.hi.web.config;

import mz.co.hi.web.users.UDetailsProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class AppConfigurations {

    private String viewsDirectory;
    private String welcomeUrl;
    private String templates[];
    private UDetailsProvider uDetailsProvider;
    private List<String> frontiers = new ArrayList<>();
    private List<String> frontierPackages = new ArrayList<>();
    private Tunnings tunnings = new Tunnings();
    private Map<String,Boolean> testFiles = new HashMap<>();
    private String defaultLanguage = "default";

    private Map<String,String> testedViews = new HashMap<>();

    private List<String> smartCachingExtensions = new ArrayList<>();

    private DeploymentMode deploymentMode = DeploymentMode.DEVELOPMENT;

    public static enum DeploymentMode {

        DEVELOPMENT, PRODUCTION

    }

    private AppConfigurations(){

        smartCachingExtensions.add("css");
        smartCachingExtensions.add("js");

    }

    public Map<String,String> getTestedViews() {
        return testedViews;
    }


    public List<String> getSmartCachingExtensions(){

        return smartCachingExtensions;

    }

    private static AppConfigurations appConfigurations = null;

    protected static void set(AppConfigurations config){

        appConfigurations = config;

    }

    public static AppConfigurations get(){

        if(appConfigurations==null)
            appConfigurations = new AppConfigurations();
        return appConfigurations;

    }



    public boolean underDevelopment(){

        return deploymentMode==DeploymentMode.DEVELOPMENT;

    }


    public String getViewsDirectory() {
        return viewsDirectory;
    }

    public void setViewsDirectory(String viewsDirectory) {
        this.viewsDirectory = viewsDirectory;
    }

    public String[] getTemplates() {
        return templates;
    }

    public void setTemplates(String[] templates) {
        this.templates = templates;
    }


    public String getWelcomeUrl() {

        return welcomeUrl;

    }

    public void setWelcomeUrl(String welcomeUrl) {
        this.welcomeUrl = welcomeUrl;
    }



    public UDetailsProvider getUserDetailsProvider() {
        return uDetailsProvider;
    }

    public void setUserDetailsProvider(UDetailsProvider uDetailsProvider) {
        this.uDetailsProvider = uDetailsProvider;
    }

    public List<String> getFrontiers() {

        return frontiers;

    }

    public void setFrontiers(List<String> frontiers) {

        this.frontiers = frontiers;

    }

    public Tunnings getTunnings() {
        return tunnings;
    }

    public void setTunnings(Tunnings tunnings) {
        this.tunnings = tunnings;
    }


    public Map<String, Boolean> getTestFiles() {
        return testFiles;
    }

    public void setTestFiles(Map<String, Boolean> testFiles) {
        this.testFiles = testFiles;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public void setDeploymentMode(DeploymentMode mode){

        this.deploymentMode = mode;

    }

    public List<String> getFrontierPackages() {
        return frontierPackages;
    }

    public void setFrontierPackages(List<String> frontierPackages) {
        this.frontierPackages = frontierPackages;
    }

    public DeploymentMode getDeploymentMode() {
        return deploymentMode;
    }
}
