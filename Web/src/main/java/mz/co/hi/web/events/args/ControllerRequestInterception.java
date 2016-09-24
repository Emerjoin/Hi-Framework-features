package mz.co.hi.web.events.args;

/**
 * @author Mário Júnior
 */
public class ControllerRequestInterception extends MethodCallInterception {


    public ControllerRequestInterception(boolean isAfter) {
        super(isAfter);
    }

    public ControllerRequestInterception(){

        super();

    }

}
