package mz.co.hi.web;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by Mario Junior.
 */
public class Helper {



    public static String readTextStreamToEnd(InputStream inputStream,RequestContext requestContext){

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

            try {

                if(requestContext !=null){

                    ex.printStackTrace(requestContext.getResponse().getWriter());

                }


            }catch (Exception exx){



            }

        }finally {

            try{

                inputStream.close();

            }catch (Exception ex){


            }

        }

        return text;


    }

    public static void echo(String text,RequestContext requestContext){


        try {


            PrintWriter printWriter = requestContext.getResponse().getWriter();
            printWriter.write(text);
            printWriter.flush();

            /*

            //PrintWriter printWriter = ActiveRequest.getResponse().getWriter();
            OutputStream outputStream = requestContext.getOutputStream();

            PrintStream ps = new PrintStream(outputStream);
            ps.write(text.getBytes("utf8"));
            ps.flush();*/


        }catch (Exception ex){

            ex.printStackTrace();

        }

    }

    public static void echoln(String text,RequestContext requestContext){

        try {


            PrintWriter printWriter = requestContext.getResponse().getWriter();
            printWriter.write(text+"\n");
            printWriter.flush();

            /*
            OutputStream outputStream = requestContext.getOutputStream();
            PrintStream ps = new PrintStream(outputStream);
            ps.println((text+"\n").getBytes("utf8"));
            ps.flush();*/


        }catch (Exception ex){


        }

    }






}
