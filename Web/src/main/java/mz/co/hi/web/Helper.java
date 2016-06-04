package mz.co.hi.web;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
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


            if(requestContext!=null){

                HttpServletResponse response = requestContext.getResponse();
                response.setContentType("charset=UTF8");
                requestContext.echo(text);

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

    public static String md5(String text){

        if(true)
            return text;


        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            //System.out.println("Digest(in hex format):: " + sb.toString());

            //convert the byte to hex format method 2
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }


            return hexString.toString();


        }catch (Exception ex){

            return text;

        }

    }






}
