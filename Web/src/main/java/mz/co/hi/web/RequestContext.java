package mz.co.hi.web;


import mz.co.hi.web.users.Sessions;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@RequestScoped
public class RequestContext {

    @Inject
    private HttpServletRequest request = null;

    private HttpServletResponse response = null;

    @Inject
    private ServletContext servletContext = null;

    private Map<String,Object> data = new HashMap<String, Object>();
    private String url = null;
    private String routeUrl;

    private OutputStream outputStream = null;

    public String getUsername(){

        return request.getRemoteUser();

    }

    public boolean isUserLogged(){

        System.out.println(~'g');

        return request.getRemoteUser()!=null;

    }

    public Map getUserDetails(){

        return Sessions.getUserDetails(getUsername());

    }

    public OutputStream getOutputStream(){

        if(outputStream ==null){

            try {

                outputStream = response.getOutputStream();


            }catch (Exception ex){



            }

        }

        return outputStream;

    }

    public String getRouteUrl(){

        return routeUrl;

    }

    /*
    public RequestContext(HttpServletRequest request, HttpServletResponse response, ServletContext context, String routeUrl){

        this.request = request;
        this.response = response;
        this.url = request.getRequestURI();
        this.servletContext = context;
        this.routeUrl = routeUrl;
    }*/

    public RequestContext(){



    }


    @PostConstruct
    private void getReady(){

        this.url = request.getRequestURI();

    }

    protected void setRouteUrl(String r){

        this.routeUrl = r;

    }

    public String getUrl() {
        return url;
    }

    public Map<String,Object> getData(){

        return data;

    }

    public void setUrl(String url) {
        this.url = url;
    }


    public ServletContext getServletContext(){

        return  servletContext;

    }



    protected  void setResponse(HttpServletResponse response){

        this.response = response;

    }

    public  HttpServletRequest getRequest(){

        return request;

    }

    public HttpServletResponse getResponse(){

        return response;

    }

    public  boolean hasAjaxHeader(){

        if(request==null){

            return false;

        }

        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

    }

    public void echo(String  str){

        Helper.echo(str,this);

    }

    public void echoln(String  str){

        Helper.echoln(str,this);

    }


    public String readToEnd(InputStream inputStream){

        return  Helper.readTextStreamToEnd(inputStream,this);

    }

    public String getBaseURL(){

        String req =this.getRequest().getRequestURL().toString();
        String contextPath = this.getRequest().getContextPath();
        int indexOfContext = req.indexOf(contextPath);
        String baseUrl = req.substring(0,indexOfContext+contextPath.length()+1);
        return baseUrl;

    }
}
