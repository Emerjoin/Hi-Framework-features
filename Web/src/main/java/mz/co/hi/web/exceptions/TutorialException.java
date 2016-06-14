package mz.co.hi.web.exceptions;


public abstract class TutorialException extends HiException {

    public TutorialException(String m) {

        super(m);

    }


    public TutorialException(String m, Throwable throwable) {

        super(m,throwable);

    }

    public abstract String getContentPath();


}
