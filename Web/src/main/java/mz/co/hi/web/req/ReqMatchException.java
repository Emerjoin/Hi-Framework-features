package mz.co.hi.web.req;

import mz.co.hi.web.HiException;

/**
 * Created by Mario Junior.
 */
public class ReqMatchException extends HiException {

    public ReqMatchException(String handler,String error){

        super("Could not finish request matching on Request Handler <"+handler+"> : "+error);

    }

}
