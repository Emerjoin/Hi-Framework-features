package mz.co.hi.web.config;

import mz.co.hi.web.exceptions.HiException;

/**
 * @author Mário Júnior
 */
public class BadConfigException extends HiException {

    public BadConfigException(String msg){

        super(msg);

    }

    public BadConfigException(String msg, Exception ex){

        super(msg,ex);

    }

}
