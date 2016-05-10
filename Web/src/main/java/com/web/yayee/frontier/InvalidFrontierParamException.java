package com.web.yayee.frontier;

import javax.servlet.ServletException;

/**
 * Created by Mario Junior.
 */
public class InvalidFrontierParamException extends ServletException {

    public InvalidFrontierParamException(String frontier, String method, String paramName){

        super("Invalid value supplied for frontier param <"+paramName+"> for method <"+method+"> on "+frontier);

    }

}
