package mz.co.hi.web;

import mz.co.hi.web.config.AppConfigurations;

/**
 * Created by Mario Junior.
 */
public class ClassLoader extends java.lang.ClassLoader {

    private static ClassLoader instance;
    //private static String controllersPackage ="example.controllers";

    private ClassLoader(){
        super();

    }

    public static ClassLoader getInstance(){

        if(instance==null){

            instance = new ClassLoader();

        }

        return instance;

    }


    public Class findController(String name){

        String capitalName = name.substring(0, 1).toUpperCase() + name.substring(1);
        String classCanonicalName = AppConfigurations.get().getControllersPackageName()+"."+capitalName;




        Class toReturn = null;

        try {


            toReturn = Class.forName(classCanonicalName);


        }catch (Exception ex){


            try{

                toReturn = Class.forName(classCanonicalName+"Controller");


            }catch (Exception exx){



            }


        }


        return toReturn;

    }

}
