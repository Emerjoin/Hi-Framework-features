package com.web.yayee.req;

import com.web.yayee.YeeException;

import javax.servlet.ServletException;

/**
 * Created by Mario Junior.
 */
public class ReqMatchException extends YeeException {

    public ReqMatchException(String handler,String error){

        super("Could not finish request matching on Request Handler <"+handler+"> : "+error);

    }

}
