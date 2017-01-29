package mz.co.hi.web.internal;

import mz.co.hi.web.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.CDI;

/**
 * @author Mário Júnior
 */
public class Logging {

    private static Logging instance = null;

    public static Logging getInstance(){

        if(instance==null)
            instance = new Logging();

        return instance;

    }

    private Logging(){ }

    public Logger getLogger(){

        String name = CDI.current().select(ConfigProvider.class).get().getLoggerName();
        return LoggerFactory.getLogger(name);

    }

}
