package mz.co.hi.web.events.listeners;

import mz.co.hi.web.events.args.ControllerRequestInterception;

/**
 * @author Mário Júnior
 */
public interface ControllerCallsListener {

    public void preAction(ControllerRequestInterception interception);
    public void postAction(ControllerRequestInterception interception);

}
