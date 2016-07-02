package mz.co.hi.web.validation;

import mz.co.hi.web.mvc.exceptions.MvcException;

/**
 * Created by Mario Junior.
 */
public class ValidationException extends MvcException {

    public ValidationException(String msg) {

        super(msg);

    }

}
