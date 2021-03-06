package mz.co.hi.web.config;

import mz.co.hi.web.exceptions.HiException;
import org.jboss.jandex.Index;
import org.slf4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Set;

/**
 * @author Mário Júnior
 */
public interface ConfigProvider {

    public void load(ServletContext servletContext, ServletConfig config, Set<Index> index) throws HiException;
    public String getDocsPath();
    public AppConfigurations getAppConfigs();

}
