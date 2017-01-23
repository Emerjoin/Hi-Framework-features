package mz.co.hi.web.events.args;

import mz.co.hi.web.events.args.Interception;
import mz.co.hi.web.mvc.Controller;

import java.lang.reflect.Method;

/**
 * @author Mário Júnior
 */
//TODO: JavaDoc
public abstract class MethodCallInterception extends Interception {

    private Class<? extends Controller> clazz = null;
    private Method method = null;

    public MethodCallInterception(boolean isAfter) {
        super(isAfter);
    }

    public MethodCallInterception(){

        super();

    }

    public Class<? extends Controller> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends Controller> clazz) {
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
