package mz.co.hi.web.events.args;

/**
 * @author Mário Júnior
 */
//TODO: JavaDoc
public class FrontierRequestInterception extends MethodCallInterception {


    public FrontierRequestInterception(boolean isAfter) {
        super(isAfter);
    }

    public FrontierRequestInterception(){

        super();

    }


}
