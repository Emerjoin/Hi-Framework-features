package mz.co.hi.web.exceptions;

/**
 * Created by Mario Junior.
 */
public class NoCDIScopeException extends TutorialException {

    public NoCDIScopeException(Class clazz){

        super("Class <"+clazz.getCanonicalName()+"> was not assigned a CDI Scope. <b>Bold</b>");

    }

    @Override
    public String getContentPath() {

        return null;

    }
}
