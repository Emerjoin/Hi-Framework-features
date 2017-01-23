package mz.co.hi.web.events.args;

/**
 * @author Mário Júnior
 */
//TODO: JavaDoc
public class ControllerRequestInterception extends MethodCallInterception {


    public ControllerRequestInterception(boolean isAfter) {
        super(isAfter);
    }

    public ControllerRequestInterception(){

        super();

    }

}
