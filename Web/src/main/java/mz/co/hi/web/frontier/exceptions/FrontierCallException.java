package mz.co.hi.web.frontier.exceptions;

import mz.co.hi.web.exceptions.HiException;

/**
 * @author Mário Júnior
 */
public class FrontierCallException extends HiException {

    private String frontierName;
    private String methodName;

    public FrontierCallException(String frontier, String method, Exception ex){

        super("An error occurred while calling the method <"+method+"> of frontier <"+frontier+">",ex);
        this.frontierName = frontier;
        this.methodName = method;

    }

    public FrontierCallException(String frontier, String method, String msg){

        super(msg);
        this.frontierName = frontier;
        this.methodName = method;

    }

    public FrontierCallException(String frontier, String method, String msg, Exception ex){

        super(msg,ex);
        this.frontierName = frontier;
        this.methodName = method;

    }


    public String getFrontierName() {
        return frontierName;
    }

    public String getMethodName() {
        return methodName;
    }
}
