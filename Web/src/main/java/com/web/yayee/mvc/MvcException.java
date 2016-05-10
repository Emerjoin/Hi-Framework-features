package com.web.yayee.mvc;

import com.web.yayee.YeeException;

import javax.servlet.ServletException;

/**
 * Created by Mario Junior.
 */
public class MvcException extends YeeException {

    public MvcException(String msg, Throwable throwable){

        super(msg,throwable);

    }

    public MvcException(String msg){

        super(msg);

    }

}
