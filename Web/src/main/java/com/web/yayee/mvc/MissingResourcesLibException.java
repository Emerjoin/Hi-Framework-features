package com.web.yayee.mvc;

import com.web.yayee.YeeException;

/**
 * Created by Mario Junior.
 */
public class MissingResourcesLibException extends YeeException {

    public MissingResourcesLibException(){

        super("The Yayee Resources Lib could not be found. Include de jar in your artifact or make it available on your application server");

    }

    public MissingResourcesLibException(Throwable cause){

        super("The Yayee Resources Lib could not be found. Include de jar in your artifact or make it available on your application server",cause);

    }

}
