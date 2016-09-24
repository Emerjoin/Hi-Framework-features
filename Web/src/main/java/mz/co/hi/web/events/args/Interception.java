package mz.co.hi.web.events.args;

/**
 * @author Mário Júnior
 */
public abstract class Interception {

    private boolean after;

    public Interception(boolean isAfter){

        this.after = isAfter;

    }

    public Interception(){



    }


    public boolean isAfter(){

        return after;

    }

    public boolean isBefore(){

        return !after;

    }

    public void setAfter(){

        this.after = true;

    }

    public void setBefore(){

        this.after = false;

    }

}
