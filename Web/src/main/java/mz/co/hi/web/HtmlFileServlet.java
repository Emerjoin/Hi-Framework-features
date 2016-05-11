package mz.co.hi.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Mario Junior.
 */
public class HtmlFileServlet extends DispatcherServlet {

    private String html = null;
    private String preHtml = null;
    private String htmlFile = null;

    public static String readTextStreamToEnd(InputStream inputStream){

        if(inputStream==null){

            return null;

        }

        String text = "";

        try {

            Scanner scanner = new Scanner(inputStream,"utf8");
            while (scanner.hasNextLine()) {

                text +=scanner.nextLine()+"\n";

            }

        }catch (Exception ex){


        }finally {

            try{

                inputStream.close();

            }catch (Exception ex){


            }

        }

        return text;


    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {


        handle(request,response);


    }



    private String changewebroot(String html, HttpServletRequest request){

        String requestURL = request.getRequestURL().toString();
        int contextPathIndex = requestURL.indexOf(request.getContextPath());

        String baseURL = requestURL.substring(0,contextPathIndex+request.getContextPath().length());

        String webrootPath = baseURL+"/webroot/";
        return html.replaceAll("webroot\\/",webrootPath);

    }

    private void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException{

        try {

            if(preHtml==null)
                return;

            if(htmlFile==null)
                return;

            response.setContentType("text/html;charset=UTF8");

            PrintWriter printWriter  = response.getWriter();

                if(html==null) {

                    html = changewebroot(preHtml, request);

                }

                printWriter.write(html);
                printWriter.flush();



        } catch (IOException e) {

            e.printStackTrace();

        }


    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException{

        handle(request,response);


    }


    public void init(ServletConfig config) throws ServletException{

        if(config!=null){

            try {

                String hfile = config.getInitParameter("filename");

                if (hfile != null) {

                    htmlFile = hfile;

                    try {

                        URL login = config.getServletContext().getResource("/" + htmlFile);

                        if (login != null) {

                            preHtml = readTextStreamToEnd(login.openStream());

                        }


                    }catch (Exception ex){



                    }



                }

            }catch (Exception ex){



            }

        }

        super.init(config);


    }

}
