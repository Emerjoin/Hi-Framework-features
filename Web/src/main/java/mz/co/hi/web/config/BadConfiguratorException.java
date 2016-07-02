package mz.co.hi.web.config;

import mz.co.hi.web.exceptions.HiException;

/**
 * @author Mário Júnior
 */
public class BadConfiguratorException extends HiException {

    public BadConfiguratorException(String message){

        super(message);

    }

    public BadConfiguratorException(String message, Throwable ex){

        super(message,ex);

    }

}
