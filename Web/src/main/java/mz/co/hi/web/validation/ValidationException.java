package mz.co.hi.web.validation;

import mz.co.hi.web.mvc.MvcException;

/**
 * Created by Mario Junior.
 */
public class ValidationException extends MvcException {

    public ValidationException(String msg) {

        super(msg);

    }

}
