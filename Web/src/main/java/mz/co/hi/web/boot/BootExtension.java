package mz.co.hi.web.boot;

import org.jboss.jandex.Index;

import javax.servlet.ServletConfig;
import java.util.Set;

/**
 * @author Mário Júnior
 */
public interface BootExtension {

    public void boot(Set<Index> indexes, ServletConfig servletConfig) throws Exception;

}
