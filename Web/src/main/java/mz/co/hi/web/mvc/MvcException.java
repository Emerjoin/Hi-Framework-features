package mz.co.hi.web.mvc;

import mz.co.hi.web.HiException;

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
