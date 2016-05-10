package com.web.yayee;

import javax.servlet.ServletException;

/**
 * Created by Mario Junior.
 */
public class YeeException extends ServletException {

    public YeeException(String m){

        super(m);

    }

    public YeeException(String m,Throwable throwable){

        super(m,throwable);

    }

}
