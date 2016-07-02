package mz.co.hi.web.frontier.exceptions;

import javax.servlet.ServletException;

/**
 * Created by Mario Junior.
 */
public class FrontierInvocationFailedException extends ServletException {

    public FrontierInvocationFailedException(String frontier, String method, Throwable throwable){

        super("The invocation of frontier method <"+method+"> on <"+frontier+"> failed. An exception thrown during invocation.",throwable);

    }

}
