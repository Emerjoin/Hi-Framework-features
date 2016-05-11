package mz.co.hi.web.notification;

import javax.servlet.ServletException;

/**
 * Created by Mario Junior.
 */
public class NotificationConversionException extends ServletException {

    public NotificationConversionException(String type, Throwable throwable){

        super("Notification object of type <"+type+"> could not be converted to JSON.",throwable);

    }

}
