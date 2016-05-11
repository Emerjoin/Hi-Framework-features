package mz.co.hi.web.frontier;

import javax.servlet.ServletException;

/**
 * Created by Mario Junior.
 */
public class MapConversionException extends ServletException {

    public MapConversionException(String frontier, String method, Throwable throwable){

        super("Returned java.util.Map object by method <"+method+"> on frontier <"+frontier+"> could not be converted to JSON.",throwable);

    }

}
