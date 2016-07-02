package mz.co.hi.web.mvc.exceptions;

import mz.co.hi.web.exceptions.HiException;

/**
 * Created by Mario Junior.
 */
public class MvcException extends HiException {

    public MvcException(String msg, Throwable throwable){

        super(msg,throwable);

    }

    public MvcException(String msg){

        super(msg);

    }

}
