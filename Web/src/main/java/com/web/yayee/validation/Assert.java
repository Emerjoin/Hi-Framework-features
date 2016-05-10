package com.web.yayee.validation;


import java.util.Enumeration;
import java.util.HashMap;

public class Assert {

    public static boolean range(int min, int max, int... params) throws ValidationException{


        if(params==null)
            throw new ValidationException("Invalid validation subjects");


        for(int p: params){

            if(!(p>=min&&p<=max)){

                throw new ValidationException("Invalid Int value <"+p+">");

            }

        }

        return true;

    }


    public static boolean range(long min, int max, long... params) throws ValidationException{

        if(params==null)
            throw new ValidationException("Invalid validation subjects");


        for(long p: params){

            if(!(p>=min&&p<=max)){

                throw new ValidationException("Invalid Long value <"+p+">");

            }

        }

        return true;


    }


    public static boolean range(float min, float max, float... params) throws ValidationException{

        if(params==null)
            throw new ValidationException("Invalid validation subjects");


        for(float p: params){

            if(!(p>=min&&p<=max)){

                throw new ValidationException("Invalid Float value <"+p+">");

            }

        }

        return true;


    }


    public static boolean range(double min, double max, double... params) throws ValidationException{

        if(params==null)
            throw new ValidationException("Invalid validation subjects");


        for(double p: params){

            if(!(p>=min&&p<=max)){

                throw new ValidationException("Invalid Int value <"+p+">");

            }

        }

        return true;

    }


    public static boolean notNull(Object... params) throws ValidationException{

        if(params==null)
            throw new ValidationException("Invalid validation subjects");


        if(params==null){
            throw new ValidationException("Invalid validation subjects");
        }

        for(Object p: params){


            if(p==null){

                throw new ValidationException("Null object instace detected!");

            }

        }

        return true;

    }

    public static boolean regExp(String pattern, String... params) throws ValidationException{

        if(params==null)
            throw new ValidationException("Invalid validation subjects");


        if(pattern==null){

            throw new ValidationException("Null regular expression");

        }

        for(String p : params){

            if(p==null){

                throw new ValidationException("Null string could not be validated using RegExp");

            }

            if(!p.matches(pattern)){

                throw new ValidationException("Regular Expression not matched");

            }

        }

        return true;


    }


    public static boolean enumeration(Class en,String... params) throws ValidationException{

        if(params==null)
            throw new ValidationException("Invalid validation subjects");

        if(!en.isEnum()){

            throw new ValidationException("Invalid enumeration type");

        }

        Object[] constants = en.getEnumConstants();

        HashMap<String,Boolean> map = new HashMap<>();
        for(Object co: constants ){

            map.put(co.toString(),true);

        }


        for(String p : params){

            if(!map.containsKey(p)){

                throw new ValidationException("Invalid enum value for type <"+en.getClass().getCanonicalName()+">");

            }

        }

        return true;

    }

    public static boolean string(int minLength, String... params) throws ValidationException{

        if(params==null)
            throw new ValidationException("Invalid validation subjects");


        if(minLength<0)
            throw new ValidationException("Invalid minimum String length");

        for(String p: params){

            if(p!=null){

                if(p.length()<minLength){

                    throw new ValidationException("Invalid String length");

                }

            }

        }


        return true;

    }


    public static boolean string(int minLength, int maxLength, String... params) throws ValidationException{

        if(params==null)
            throw new ValidationException("Invalid validation subjects");


        if(minLength<0)
            throw new ValidationException("Invalid minimum String length");

        if(maxLength<0)
            throw new ValidationException("Invalid maximum String length");


        for(String p: params){

            if(p!=null){

                if(p.length()<minLength||p.length()>maxLength){

                    throw new ValidationException("Invalid String length");

                }

            }

        }

        return true;

    }


}
