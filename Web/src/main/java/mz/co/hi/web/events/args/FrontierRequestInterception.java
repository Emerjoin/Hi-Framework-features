package mz.co.hi.web.events.args;

/**
 * @author Mário Júnior
 */
public class FrontierRequestInterception extends MethodCallInterception {


    public FrontierRequestInterception(boolean isAfter) {
        super(isAfter);
    }

    public FrontierRequestInterception(){

        super();

    }


}
