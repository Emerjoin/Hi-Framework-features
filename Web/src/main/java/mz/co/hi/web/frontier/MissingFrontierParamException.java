package mz.co.hi.web.frontier;

import javax.servlet.ServletException;

/**
 * Created by Mario Junior.
 */
public class MissingFrontierParamException extends ServletException {

    public MissingFrontierParamException(String frontier, String method, String paramName){

        super("No value supplied for frontier param <"+paramName+"> for method <"+method+"> on "+frontier);

    }


}
